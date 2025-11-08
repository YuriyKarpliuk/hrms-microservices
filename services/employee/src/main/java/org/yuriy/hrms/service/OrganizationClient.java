package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "organization-service")
public interface OrganizationClient {

    @GetMapping("/api/v1/organizations/{id}/name")
    String getNameById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/organizations/{id}")
    OrganizationResponse getOrganizationById(@PathVariable("id") Long id);

    record OrganizationResponse(Long id, String name, String alias, String address) {}
}
