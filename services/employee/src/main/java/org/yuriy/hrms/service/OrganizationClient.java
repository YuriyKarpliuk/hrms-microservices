package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.request.OrganizationCreateRequest;
import org.yuriy.hrms.dto.request.OrganizationPatchRequest;
import org.yuriy.hrms.dto.request.OrganizationSearchRequest;
import org.yuriy.hrms.dto.response.PageResponse;

import java.util.List;

@FeignClient(name = "organization-service")
public interface OrganizationClient {

    @GetMapping("/api/v1/organizations/{id}/name")
    String getNameById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/organizations/{id}")
    OrganizationResponse getOrganizationById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/organizations")
    List<OrganizationResponse> getAll();

    @PostMapping("/api/v1/organizations/search/new")
    PageResponse<OrganizationResponse> search(@RequestBody OrganizationSearchRequest req,   @RequestParam int page,
            @RequestParam int size);

    @PostMapping("/api/v1/organizations")
    OrganizationResponse create(@RequestBody OrganizationCreateRequest req);

    @PatchMapping("/api/v1/organizations/{id}")
    OrganizationResponse update(@PathVariable Long id,
            @RequestBody OrganizationPatchRequest req);

    @DeleteMapping("/api/v1/organizations/{id}")
    void delete(@PathVariable Long id);

     record OrganizationResponse(Long id,
            String name,
            String website,
            String currency,
            String taxNumber,
            List<OrganizationAddressResponse> addresses) {
    }
     record OrganizationAddressResponse(
            Long id,
            String country,
            String city,
            String street,
            String zipCode
    ) {
    }
}

