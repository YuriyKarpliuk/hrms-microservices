package org.yuriy.hrms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.request.EmployeeSearchRequest;
import org.yuriy.hrms.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    Page<EmployeeResponse> searchEmployees(EmployeeSearchRequest request, Pageable pageable);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse createNewEmployee(EmployeeCreateRequest req);

    EmployeeResponse updateEmployee(Long id, EmployeeCreateRequest req);

    EmployeeResponse patch(Long id, EmployeePatchRequest req);

    void deleteEmployee(Long id);
}
