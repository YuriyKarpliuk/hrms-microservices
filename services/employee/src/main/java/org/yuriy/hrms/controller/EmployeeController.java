package org.yuriy.hrms.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.mapper.EmployeeMapper;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.response.EmployeeResponse;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.service.EmployeeService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Validated
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeMapper mapper;

    @Autowired
    public EmployeeController(EmployeeService service, EmployeeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EmployeeResponse> getEmployeesByOrgAndStatus(@RequestParam @NotNull Long orgId,
            @RequestParam(required = false) Employee.Status status) {
        return service.getEmployeesByOrgAndStatus(orgId, status)
                .stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return mapper.toResponse(service.getEmployeeById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createNewEmployee(@Valid @RequestBody EmployeeCreateRequest req) {
        Employee saved = service.createNewEmployee(req);
        return ResponseEntity
                .created(URI.create("/api/v1/employees/" + saved.getId()))
                .body(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public EmployeeResponse employeeFullUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeeCreateRequest req) {
        return mapper.toResponse(service.updateEmployee(id, req));
    }

    @PatchMapping("/{id}")
    public EmployeeResponse employeePartialUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeePatchRequest req) {
        return mapper.toResponse(service.patch(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
