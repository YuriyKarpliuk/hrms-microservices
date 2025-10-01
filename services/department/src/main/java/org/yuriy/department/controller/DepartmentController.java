package org.yuriy.department.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.department.dto.request.DepartmentCreateRequest;
import org.yuriy.department.dto.request.DepartmentPatchRequest;
import org.yuriy.department.dto.request.DepartmentSearchRequest;
import org.yuriy.department.dto.response.DepartmentResponse;
import org.yuriy.department.service.DepartmentService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/departments")
@Validated
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {this.departmentService = departmentService;}

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<DepartmentResponse> createNewDepartment(@Valid @RequestBody DepartmentCreateRequest req) {
        return new ResponseEntity<>(departmentService.createNewDepartment(req), HttpStatus.CREATED);
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<DepartmentResponse> departmentPartialUpdate(@PathVariable Long id,
            @Valid @RequestBody DepartmentPatchRequest req) {
        return ResponseEntity.ok(departmentService.partialUpdateDepartment(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DepartmentResponse>> searchDepartments(
            @RequestBody DepartmentSearchRequest request,
            @PageableDefault(sort = "name") Pageable pageable) {
        return ResponseEntity.ok(departmentService.searchDepartments(request, pageable));
    }
}
