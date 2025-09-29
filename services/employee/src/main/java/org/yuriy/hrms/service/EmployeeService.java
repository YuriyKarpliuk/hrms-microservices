package org.yuriy.hrms.service;

import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse createNewEmployee(EmployeeCreateRequest req);

    EmployeeResponse updateEmployee(Long id, EmployeeCreateRequest req);

    EmployeeResponse patch(Long id, EmployeePatchRequest req);

    void deleteEmployee(Long id);
}
