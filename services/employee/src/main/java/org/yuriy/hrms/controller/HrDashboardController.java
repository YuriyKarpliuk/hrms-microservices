package org.yuriy.hrms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yuriy.hrms.dto.response.HrDashboardResponse;
import org.yuriy.hrms.service.HrDashboardService;

@RestController
@RequestMapping("/api/v1/hr/dashboard")
@RequiredArgsConstructor
public class HrDashboardController {

    private final HrDashboardService dashboardService;

    @GetMapping("/{hrId}")
    public ResponseEntity<HrDashboardResponse> getDashboard(@PathVariable Long hrId) {
        return ResponseEntity.ok(dashboardService.getDashboard(hrId));
    }
}
