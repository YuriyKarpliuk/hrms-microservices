package org.yuriy.payrollservice.service.impl;


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
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;

    private final PayrollMapper payrollMapper;

    private final EmployeeClient employeeClient;

    public PayrollServiceImpl(PayrollRepository payrollRepository, PayrollMapper payrollMapper,
            EmployeeClient employeeClient) {
        this.payrollRepository = payrollRepository;
        this.payrollMapper = payrollMapper;
        this.employeeClient = employeeClient;
    }

    @Override
    @Transactional
    public PayrollWithEmployeeResponse createPayroll(PayrollCreateRequest r) {

        if (!employeeClient.existsById(r.employeeId())) {
            throw new IllegalArgumentException("Employee with id " + r.employeeId() + " not found");
        }

        EmployeeBasicResponse emp = employeeClient.getBasicInfo(r.employeeId());


        Payroll payroll = payrollMapper.toEntity(r);
        return payrollMapper.toWithEmployeeResponse(payrollRepository.save(payroll), emp);
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
        return payrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Override
    @Transactional
    public PayrollResponse markAsFailed(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found with id " + id));
        payroll.setStatus(PayrollStatus.FAILED);
        return payrollMapper.toResponse(payrollRepository.save(payroll));
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

    private BigDecimal calculateDeduction(String leaveType, BigDecimal dailyRate, long days) {
        return switch (leaveType.toUpperCase()) {
            case "UNPAID" -> dailyRate.multiply(BigDecimal.valueOf(days));
            default -> BigDecimal.ZERO;
        };
    }
}
