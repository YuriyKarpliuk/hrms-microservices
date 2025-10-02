package org.yuriy.organization.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.organization.dto.request.OrganizationCreateRequest;
import org.yuriy.organization.dto.request.OrganizationPatchRequest;
import org.yuriy.organization.dto.request.OrganizationSearchRequest;
import org.yuriy.organization.dto.response.OrganizationResponse;
import org.yuriy.organization.service.OrganizationService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/organizations")
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> createNewOrganization(
            @Valid @RequestBody OrganizationCreateRequest req) {
        return new ResponseEntity<>(organizationService.createNewOrganization(req), HttpStatus.CREATED);
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> organizationPartialUpdate(@PathVariable Long id,
            @Valid @RequestBody OrganizationPatchRequest req) {
        return ResponseEntity.ok(organizationService.partialUpdateOrganization(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<OrganizationResponse>> searchOrganizations(
            @RequestBody OrganizationSearchRequest request,
            @PageableDefault(sort = "name") Pageable pageable) {
        return ResponseEntity.ok(organizationService.searchOrganizations(request, pageable));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.existsById(id));
    }
}
