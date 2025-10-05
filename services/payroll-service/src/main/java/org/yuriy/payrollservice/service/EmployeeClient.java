package org.yuriy.payrollservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yuriy.payrollservice.dto.response.EmployeeBasicResponse;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @GetMapping("/api/v1/employees/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/employees/{id}/basic")
    EmployeeBasicResponse getBasicInfo(@PathVariable("id") Long id);
}
