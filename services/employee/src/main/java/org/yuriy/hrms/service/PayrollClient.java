package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.yuriy.hrms.dto.response.PayrollResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(name = "payroll-service")
public interface PayrollClient {
    @GetMapping("/api/v1/payrolls/employee/{employeeId}")
    List<Map<String, Object>> getPayrollsByEmployee(@PathVariable Long employeeId);

    @GetMapping("/api/v1/payrolls/team")
    Page<PayrollResponse> getTeamPayrolls(
            @RequestParam("managerId") Long managerId,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "startFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startFrom,
            @RequestParam(value = "endTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTo,
            Pageable pageable
    );
}
