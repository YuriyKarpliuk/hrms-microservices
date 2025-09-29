package org.yuriy.hrms.service;

import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getEmployeesByOrgAndStatus(Long orgId, Employee.Status status);

    Employee getEmployeeById(Long id);

    Employee createNewEmployee(EmployeeCreateRequest req);

    Employee updateEmployee(Long id, EmployeeCreateRequest req);

    Employee patch(Long id, EmployeePatchRequest req);

    void deleteEmployee(Long id);
}
