package org.yuriy.payrollservice.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.payrollservice.dto.mapper.PayrollMapper;
import org.yuriy.payrollservice.dto.request.PayrollCreateRequest;
import org.yuriy.payrollservice.dto.request.PayrollSearchRequest;
import org.yuriy.payrollservice.dto.response.EmployeeBasicResponse;
import org.yuriy.payrollservice.dto.response.PayrollResponse;
import org.yuriy.payrollservice.dto.response.PayrollWithEmployeeResponse;
import org.yuriy.payrollservice.entity.Payroll;
import org.yuriy.payrollservice.entity.PayrollStatus;
import org.yuriy.payrollservice.kafka.PayrollCreatedEvent;
import org.yuriy.payrollservice.kafka.PayrollEventProducer;
import org.yuriy.payrollservice.kafka.PayrollFailedEvent;
import org.yuriy.payrollservice.kafka.PayrollPayedEvent;
import org.yuriy.payrollservice.repository.PayrollRepository;
import org.yuriy.payrollservice.repository.specification.PayrollSpecification;
import org.yuriy.payrollservice.service.EmployeeClient;
import org.yuriy.payrollservice.service.PayrollService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;

    private final PayrollMapper payrollMapper;

    private final EmployeeClient employeeClient;

    private final PayrollEventProducer payrollEventProducer;


    @Override
    @Transactional
    public PayrollWithEmployeeResponse createPayroll(PayrollCreateRequest r) {

        if (!employeeClient.existsById(r.employeeId())) {
            throw new IllegalArgumentException("Employee with id " + r.employeeId() + " not found");
        }

        EmployeeBasicResponse emp = employeeClient.getBasicInfo(r.employeeId());


        Payroll payroll = payrollMapper.toEntity(r);
        payrollRepository.save(payroll);
        payrollEventProducer.sendPayrollCreated(
                new PayrollCreatedEvent(payroll.getId(), payroll.getEmployeeId(), emp.email(),
                        payroll.getStatus().toString(), payroll.getPeriodStart(), payroll.getPeriodEnd()));
        return payrollMapper.toWithEmployeeResponse(payroll, emp);
    }

    @Override
    public List<PayrollResponse> getAllPayrolls() {
        return payrollRepository.findAll().stream().map(payrollMapper::toResponse).toList();
    }

    @Override
    public PayrollResponse getPayrollById(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found with id " + id));
        return payrollMapper.toResponse(payroll);
    }

    @Override
    @Transactional
    public PayrollResponse markAsPaid(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found with id " + id));
        payroll.setStatus(PayrollStatus.PAID);
        payrollRepository.save(payroll);

        EmployeeBasicResponse emp = employeeClient.getBasicInfo(payroll.getEmployeeId());

        payrollEventProducer.sendPayrollPayed(
                new PayrollPayedEvent(payroll.getId(), payroll.getEmployeeId(), emp.email(),
                        payroll.getStatus().toString(), payroll.getPeriodStart(), payroll.getPeriodEnd()));
        return payrollMapper.toResponse(payroll);
    }

    @Override
    @Transactional
    public PayrollResponse markAsFailed(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found with id " + id));
        payroll.setStatus(PayrollStatus.FAILED);
        payrollRepository.save(payroll);
        EmployeeBasicResponse emp = employeeClient.getBasicInfo(payroll.getEmployeeId());
        payrollEventProducer.sendPayrollFailed(
                new PayrollFailedEvent(payroll.getId(), payroll.getEmployeeId(), emp.email(),
                        payroll.getStatus().toString(), payroll.getPeriodStart(), payroll.getPeriodEnd()));
        return payrollMapper.toResponse(payroll);
    }

    @Override
    public Page<PayrollResponse> searchPayrolls(PayrollSearchRequest request, Pageable pageable) {
        List<Specification<Payroll>> specifications = new ArrayList<>();

        if (request.employeeId() != null) {
            specifications.add(PayrollSpecification.hasEmployee(request.employeeId()));
        }

        if (request.fromDate() != null || request.toDate() != null) {
            specifications.add(PayrollSpecification.periodOverlaps(request.fromDate(), request.toDate()));
        }

        if (request.status() != null) {
            specifications.add(PayrollSpecification.hasStatus(request.status()));
        }

        if (request.minAmount() != null || request.maxAmount() != null) {
            specifications.add(PayrollSpecification.netSalaryBetween(request.minAmount(), request.maxAmount()));
        }

        Specification<Payroll> specification = Specification.allOf(specifications);

        return payrollRepository.findAll(specification, pageable).map(payrollMapper::toResponse);
    }

    @Override
    public List<PayrollResponse> getPayrollsByEmployee(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(payrollMapper::toResponse)
                .toList();
    }

    @Override
    public void applyLeaveToPayroll(Long employeeId, LocalDate startDate, LocalDate endDate, String type) {
        long leaveDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        payrollRepository.findByEmployeeIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(employeeId, startDate,
                        endDate)
                .ifPresentOrElse(payroll -> {
                    BigDecimal dailyRate = payroll.getBaseSalary().divide(BigDecimal.valueOf(
                                    ChronoUnit.DAYS.between(payroll.getPeriodStart(), payroll.getPeriodEnd()) + 1),
                            BigDecimal.ROUND_HALF_UP);

                    BigDecimal deduction = calculateDeduction(type, dailyRate, leaveDays);

                    BigDecimal newDeductions = payroll.getDeductions() == null
                            ? deduction
                            : payroll.getDeductions().add(deduction);

                    payroll.setDeductions(newDeductions);
                    payroll.setNetSalary(payroll.getBaseSalary().add(
                            payroll.getBonus() != null ? payroll.getBonus() : BigDecimal.ZERO
                    ).subtract(newDeductions));

                    payrollRepository.save(payroll);

                    log.info("Applied leave deduction for emp={}, days={}, type={}, amount={}",
                            employeeId, leaveDays, type, deduction);
                }, () -> log.warn("No payroll found for employee {} covering {} - {}", employeeId, startDate, endDate));
    }

    @Override
    public Page<PayrollResponse> searchPayrollsForManager(Long managerId, PayrollSearchRequest req, Pageable pageable) {
        List<EmployeeBasicResponse> team = employeeClient.getEmployeesByManager(managerId);

        if (req.employeeName() != null && !req.employeeName().isBlank()) {
            String query = req.employeeName().toLowerCase();
            team = team.stream()
                    .filter(e -> (e.firstName() + " " + e.lastName()).toLowerCase().contains(query))
                    .toList();
        }

        List<Long> employeeIds = team.stream().map(EmployeeBasicResponse::id).toList();
        if (employeeIds.isEmpty()) return Page.empty(pageable);

        List<Specification<Payroll>> specs = new ArrayList<>();
        specs.add(PayrollSpecification.employeeIn(employeeIds));

        if (req.employeeId() != null)
            specs.add(PayrollSpecification.hasEmployee(req.employeeId()));

        if (req.status() != null)
            specs.add(PayrollSpecification.hasStatus(req.status()));

        if (req.fromDate() != null || req.toDate() != null)
            specs.add(PayrollSpecification.periodOverlaps(req.fromDate(), req.toDate()));

        if (req.minAmount() != null || req.maxAmount() != null)
            specs.add(PayrollSpecification.netSalaryBetween(req.minAmount(), req.maxAmount()));

        Specification<Payroll> spec = Specification.allOf(specs);

        return payrollRepository.findAll(spec, pageable).map(payrollMapper::toResponse);
    }



    private BigDecimal calculateDeduction(String leaveType, BigDecimal dailyRate, long days) {
        return switch (leaveType.toUpperCase()) {
            case "UNPAID" -> dailyRate.multiply(BigDecimal.valueOf(days));
            default -> BigDecimal.ZERO;
        };
    }
}
