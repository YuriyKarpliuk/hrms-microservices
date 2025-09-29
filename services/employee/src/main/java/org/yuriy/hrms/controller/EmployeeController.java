package org.yuriy.hrms.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
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
    public ResponseEntity<EmployeeResponse> createNewEmployee(@Valid @RequestBody EmployeeCreateRequest req) {
        return new ResponseEntity<>(employeeService.createNewEmployee(req), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> employeeFullUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeeCreateRequest req) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponse> employeePartialUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeePatchRequest req) {
        return ResponseEntity.ok(employeeService.patch(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
