package org.yuriy.notificationservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yuriy.notificationservice.dto.response.OrganizationResponse;

import java.util.List;


@FeignClient(name = "organization-service")
public interface OrganizationClient {
    @GetMapping("/api/v1/organizations/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/organizations")
    List<OrganizationResponse> getAllOrganizations();

    @GetMapping("/api/v1/organizations/{id}")
    OrganizationResponse getById(@PathVariable Long id);
}
