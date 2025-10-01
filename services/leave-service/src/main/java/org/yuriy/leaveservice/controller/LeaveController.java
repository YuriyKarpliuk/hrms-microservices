package org.yuriy.leaveservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.leaveservice.dto.request.LeaveCreateRequest;
import org.yuriy.leaveservice.dto.request.LeaveSearchRequest;
import org.yuriy.leaveservice.dto.response.LeaveResponse;
import org.yuriy.leaveservice.service.LeaveService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/leaves")
@Validated
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {this.leaveService = leaveService;}

    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(@Validated @RequestBody LeaveCreateRequest req) {
        return ResponseEntity.ok(leaveService.createLeave(req));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #employeeId == authentication.principal.claims['employeeId']")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{leaveId}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long leaveId) {
        return ResponseEntity.ok(leaveService.approveLeave(leaveId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{leaveId}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable Long leaveId) {
        return ResponseEntity.ok(leaveService.rejectLeave(leaveId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LeaveResponse>> searchLeaves(
            @RequestBody LeaveSearchRequest request,
            @PageableDefault(sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(leaveService.searchLeaves(request, pageable));
    }

}
