package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "department-service")
public interface DepartmentClient {
    @GetMapping("/api/v1/departments/{id}/name")
    String getNameById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/departments/{id}")
    DepartmentResponse getDepartmentById(@PathVariable("id") Long id);

    record DepartmentResponse(Long id, String name, Long orgId) {}
}
