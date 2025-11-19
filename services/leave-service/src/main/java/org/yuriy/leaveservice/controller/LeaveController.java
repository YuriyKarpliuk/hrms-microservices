package org.yuriy.leaveservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.leaveservice.dto.request.LeaveCreateRequest;
import org.yuriy.leaveservice.dto.request.LeaveSearchRequest;
import org.yuriy.leaveservice.dto.response.LeaveResponse;
import org.yuriy.leaveservice.dto.response.LeaveSummaryResponse;
import org.yuriy.leaveservice.dto.response.LeaveUpcomingResponse;
import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;
import org.yuriy.leaveservice.service.LeaveService;
import org.yuriy.leaveservice.dto.response.ApprovedLeaveDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/leaves")
@Validated
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {this.leaveService = leaveService;}

    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(@RequestBody LeaveCreateRequest req) {
        return ResponseEntity.ok(leaveService.requestLeave(req));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @employeeSecurity.isOwner(authentication)")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{leaveId}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long leaveId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long managerId = jwt.getClaim("employeeId");
        return ResponseEntity.ok(leaveService.approveLeave(leaveId, managerId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{leaveId}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable Long leaveId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long managerId = jwt.getClaim("employeeId");
        return ResponseEntity.ok(leaveService.rejectLeave(leaveId, managerId));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<LeaveResponse>> searchLeaves(
            @RequestBody LeaveSearchRequest request,
            @PageableDefault(sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(leaveService.searchLeaves(request, pageable));
    }

    @PreAuthorize("@employeeSecurity.isOwner(authentication)")
    @GetMapping("/employee/{employeeId}/summary")
    public ResponseEntity<List<LeaveSummaryResponse>> getLeaveSummary(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeaveSummary(employeeId));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ApprovedLeaveDto>> getApprovedLeaves(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LeaveSearchRequest request = new LeaveSearchRequest(
                employeeId,
                null,
                LeaveStatus.APPROVED,
                null,
                from,
                null,
                to,
                null,
                null
        );

        Page<LeaveResponse> results = leaveService.searchLeaves(request, Pageable.unpaged());

        List<ApprovedLeaveDto> response = new ArrayList<>();
        for (LeaveResponse leave : results.getContent()) {
            LocalDate date = leave.startDate();
            while (!date.isAfter(leave.endDate())) {
                response.add(new ApprovedLeaveDto(date, leave.type().name()));
                date = date.plusDays(1);
            }
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{employeeId}/remaining-days")
    public ResponseEntity<Double> getRemainingDays(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getRemainingDays(employeeId));
    }

    @GetMapping("/employee/{employeeId}/upcoming")
    public ResponseEntity<List<LeaveUpcomingResponse>> getUpcoming(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getUpcomingLeaves(employeeId));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<Page<LeaveResponse>> getTeamLeaves(
            @RequestParam Long managerId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTo,
            @PageableDefault Pageable pageable
    ) {
        LeaveSearchRequest request = new LeaveSearchRequest(
                employeeId,
                employeeName,
                status != null ? LeaveStatus.valueOf(status) : null,
                type != null ? LeaveType.valueOf(type) : null,
                startFrom, null, null, endTo, managerId
        );

        return ResponseEntity.ok(leaveService.searchLeavesForManager(request, pageable));
    }

}
