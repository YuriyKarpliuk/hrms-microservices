package org.yuriy.department.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "organization-service")
public interface OrganizationClient {
    @GetMapping("/api/v1/organizations/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}
