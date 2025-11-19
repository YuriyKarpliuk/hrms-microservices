package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.yuriy.hrms.dto.response.LeaveResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(name = "leave-service")
public interface LeaveClient {

    @GetMapping("/api/v1/leaves/employee/{employeeId}/summary")
    List<Map<String, Object>> getLeaveSummary(@PathVariable Long employeeId);

    @GetMapping("/api/v1/leaves/employee/{employeeId}/upcoming")
    List<Map<String, Object>> getUpcoming(@PathVariable Long employeeId);

    @GetMapping("/api/v1/leaves/team")
    Page<LeaveResponse> getTeamLeaves(
            @RequestParam("managerId") Long managerId,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startFrom,
            @RequestParam(value = "endTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTo,
            Pageable pageable
    );
}
