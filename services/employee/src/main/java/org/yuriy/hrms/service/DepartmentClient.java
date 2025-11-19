package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.request.DepartmentCreateRequest;
import org.yuriy.hrms.dto.request.DepartmentPatchRequest;
import org.yuriy.hrms.dto.request.DepartmentSearchRequest;
import org.yuriy.hrms.dto.response.PageResponse;

import java.util.List;

@FeignClient(name = "department-service")
public interface DepartmentClient {

    @GetMapping("/api/v1/departments/{id}/name")
    String getNameById(@PathVariable Long id);

    @GetMapping("/api/v1/departments/{id}")
    DepartmentResponse getById(@PathVariable Long id);

    @GetMapping("/api/v1/departments")
    List<DepartmentResponse> getAll();

    @GetMapping("/api/v1/departments/organization/{orgId}")
    List<DepartmentResponse> getByOrganization(@PathVariable Long orgId);

    @PostMapping("/api/v1/departments/search/new")
    PageResponse<DepartmentResponse> search(
            @RequestBody DepartmentSearchRequest req,
            @RequestParam int page,
            @RequestParam int size
    );
    @PostMapping("/api/v1/departments")
    DepartmentResponse create(@RequestBody DepartmentCreateRequest req);

    @PatchMapping("/api/v1/departments/{id}")
    DepartmentResponse update(@PathVariable Long id,
            @RequestBody DepartmentPatchRequest req);

    @DeleteMapping("/api/v1/departments/{id}")
    void delete(@PathVariable Long id);

    record DepartmentResponse(Long id, Long orgId, String name, Long parentId, Long managerId) {}
}
