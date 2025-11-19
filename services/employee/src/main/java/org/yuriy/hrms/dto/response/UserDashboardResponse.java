package org.yuriy.hrms.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record UserDashboardResponse(
        String employeeName,
        String departmentName,
        BigDecimal currentSalary,
        double hoursThisWeek,
        double hoursTarget,
        double vacationDaysLeft,
        double sickLeaveLeft,
        double unpaidLeaveLeft,
        double vacationDaysUsed,
        List<PayrollSummary> recentPayrolls,
        List<LeaveUpcomingResponse> upcomingLeaves
) {}


