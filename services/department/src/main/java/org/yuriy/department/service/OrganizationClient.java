package org.yuriy.department.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "organization-service",  url = "${application.config.organization-url}")
public interface OrganizationClient {
    @GetMapping("/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}
