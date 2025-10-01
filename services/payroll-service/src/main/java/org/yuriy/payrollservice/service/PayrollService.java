package org.yuriy.payrollservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yuriy.payrollservice.dto.request.PayrollCreateRequest;
import org.yuriy.payrollservice.dto.request.PayrollSearchRequest;
import org.yuriy.payrollservice.dto.response.PayrollResponse;
import org.yuriy.payrollservice.dto.response.PayrollWithEmployeeResponse;

import java.util.List;

public interface PayrollService {

    PayrollWithEmployeeResponse createPayroll(PayrollCreateRequest r);

    List<PayrollResponse> getAllPayrolls();

    PayrollResponse getPayrollById(Long id);

    PayrollResponse markAsPaid(Long id);

    PayrollResponse markAsFailed(Long id);

    Page<PayrollResponse> searchPayrolls(PayrollSearchRequest request, Pageable pageable);

    List<PayrollResponse> getPayrollsByEmployee(Long employeeId);
}
