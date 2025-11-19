package org.yuriy.notificationservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yuriy.notificationservice.dto.response.EmployeeBasicResponse;

import java.util.List;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @GetMapping("/api/v1/employees/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/employees/{id}/basic")
    EmployeeBasicResponse getEmployeeBasicInfo(@PathVariable Long id);

    @GetMapping("/api/v1/employees/org/{orgId}/birthdays/today")
    List<EmployeeBasicResponse> getTodayBirthdaysForOrg(@PathVariable Long orgId);

    @GetMapping("/api/v1/employees/organization/{orgId}")
    List<EmployeeBasicResponse> getAllEmployeesOfOrg(@PathVariable Long orgId);
}

