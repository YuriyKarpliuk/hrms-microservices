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

}
