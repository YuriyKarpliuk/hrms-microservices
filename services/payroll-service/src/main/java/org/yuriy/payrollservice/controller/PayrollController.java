package org.yuriy.payrollservice.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.payrollservice.dto.request.PayrollCreateRequest;
import org.yuriy.payrollservice.dto.request.PayrollSearchRequest;
import org.yuriy.payrollservice.dto.response.PayrollResponse;
import org.yuriy.payrollservice.dto.response.PayrollWithEmployeeResponse;
import org.yuriy.payrollservice.service.PayrollService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@Validated
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {this.payrollService = payrollService;}

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<PayrollWithEmployeeResponse> createPayroll(@RequestBody PayrollCreateRequest r) {
        return ResponseEntity.ok(payrollService.createPayroll(r));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<PayrollResponse> getPayrollById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<PayrollResponse> payPayroll(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.markAsPaid(id));
    }

    @PutMapping("/{id}/fail")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<PayrollResponse> failPayroll(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.markAsFailed(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Page<PayrollResponse>> searchPayrolls(
            @RequestBody PayrollSearchRequest request,
            @PageableDefault(sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(payrollService.searchPayrolls(request, pageable));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR') or #employeeId == authentication.principal.claims['employeeId']")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
    }

}
