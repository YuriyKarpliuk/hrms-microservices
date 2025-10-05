package org.yuriy.timesheetservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.service.TimesheetService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/timesheets")
@Validated
public class TimesheetController {

    private final TimesheetService timesheetService;

    public TimesheetController(TimesheetService timesheetService) {this.timesheetService = timesheetService;}

    @PostMapping
    public ResponseEntity<TimesheetResponse> create(@Valid @RequestBody TimesheetCreateRequest req) {
        return ResponseEntity.ok(timesheetService.createTimesheet(req));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN') or #employeeId == authentication.principal.claims['employeeId']")
    public ResponseEntity<List<TimesheetResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(timesheetService.getTimesheetsByEmployee(employeeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<TimesheetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(timesheetService.getTimesheetById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Page<TimesheetResponse>> searchTimesheets(
            @RequestBody TimesheetSearchRequest request,
            @PageableDefault(sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(timesheetService.searchTimesheets(request, pageable));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<TimesheetResponse> approveTimesheet(@PathVariable Long id) {
        return ResponseEntity.ok(timesheetService.approveTimesheet(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<TimesheetResponse> rejectTimesheet(@PathVariable Long id) {
        return ResponseEntity.ok(timesheetService.rejectTimesheet(id));
    }

}
