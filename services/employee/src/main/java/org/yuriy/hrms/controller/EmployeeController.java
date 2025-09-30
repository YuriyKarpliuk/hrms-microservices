package org.yuriy.hrms.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.request.EmployeeSearchRequest;
import org.yuriy.hrms.dto.response.EmployeeResponse;
import org.yuriy.hrms.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<EmployeeResponse> createNewEmployee(@Valid @RequestBody EmployeeCreateRequest req) {
        return new ResponseEntity<>(employeeService.createNewEmployee(req), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<EmployeeResponse> employeeFullUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeeCreateRequest req) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, req));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<EmployeeResponse> employeePartialUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeePatchRequest req) {
        return ResponseEntity.ok(employeeService.patch(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            @RequestBody EmployeeSearchRequest request,
            @PageableDefault(sort = "email") Pageable pageable) {
        return ResponseEntity.ok(employeeService.searchEmployees(request, pageable));
    }

}
