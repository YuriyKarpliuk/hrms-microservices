package org.yuriy.hrms.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.request.*;
import org.yuriy.hrms.dto.response.AdminDashboardResponse;
import org.yuriy.hrms.dto.response.PageResponse;
import org.yuriy.hrms.service.AdminService;
import org.yuriy.hrms.service.DepartmentClient;
import org.yuriy.hrms.service.OrganizationClient;

import java.util.List;
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/organizations")
    public List<OrganizationClient.OrganizationResponse> organizations() {
        return adminService.getOrganizations();
    }

    @PostMapping("/organizations")
    public OrganizationClient.OrganizationResponse createOrg(@RequestBody OrganizationCreateRequest req) {
        return adminService.createOrganization(req);
    }

    @PatchMapping("/organizations/{id}")
    public OrganizationClient.OrganizationResponse updateOrg(
            @PathVariable Long id,
            @RequestBody OrganizationPatchRequest req) {
        return adminService.updateOrganization(id, req);
    }

    @DeleteMapping("/organizations/{id}")
    public void deleteOrg(@PathVariable Long id) {
        adminService.deleteOrganization(id);
    }

    @PostMapping("/organizations/search")
    public PageResponse<OrganizationClient.OrganizationResponse> searchOrgs(
            @RequestBody OrganizationSearchRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return adminService.searchOrganizations(req, page, size);
    }


    @GetMapping("/departments")
    public List<DepartmentClient.DepartmentResponse> departments() {
        return adminService.getDepartments();
    }

    @GetMapping("/departments/by-org/{orgId}")
    public List<DepartmentClient.DepartmentResponse> depsByOrg(@PathVariable Long orgId) {
        return adminService.getDepartmentsByOrg(orgId);
    }

    @PostMapping("/departments/search")
    public PageResponse<DepartmentClient.DepartmentResponse> searchDeps(
            @RequestBody DepartmentSearchRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return adminService.searchDepartments(req, page, size);
    }

    @PostMapping("/departments")
    public DepartmentClient.DepartmentResponse createDept(@RequestBody DepartmentCreateRequest req) {
        return adminService.createDepartment(req);
    }

    @PatchMapping("/departments/{id}")
    public DepartmentClient.DepartmentResponse updateDept(
            @PathVariable Long id,
            @RequestBody DepartmentPatchRequest req) {
        return adminService.updateDepartment(id, req);
    }

    @DeleteMapping("/departments/{id}")
    public void deleteDept(@PathVariable Long id) {
        adminService.deleteDepartment(id);
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboard();
    }


}

