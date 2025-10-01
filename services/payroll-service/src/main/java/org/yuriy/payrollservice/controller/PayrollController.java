package org.yuriy.payrollservice.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.payrollservice.dto.request.PayrollCreateRequest;
import org.yuriy.payrollservice.dto.request.PayrollSearchRequest;
import org.yuriy.payrollservice.dto.response.PayrollResponse;
import org.yuriy.payrollservice.service.PayrollService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@Validated
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {this.payrollService = payrollService;}

    @PostMapping
    public ResponseEntity<PayrollResponse> createPayroll(@RequestBody PayrollCreateRequest r) {
        return ResponseEntity.ok(payrollService.createPayroll(r));
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponse> getPayrollById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<PayrollResponse> payPayroll(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.markAsPaid(id));
    }

    @PutMapping("/{id}/fail")
    public ResponseEntity<PayrollResponse> failPayroll(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.markAsFailed(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PayrollResponse>> searchPayrolls(
            @RequestBody PayrollSearchRequest request,
            @PageableDefault(sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(payrollService.searchTimesheets(request, pageable));
    }

}
