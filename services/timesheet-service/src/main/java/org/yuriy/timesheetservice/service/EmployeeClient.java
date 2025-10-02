package org.yuriy.timesheetservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @GetMapping("/api/v1/employees/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}
