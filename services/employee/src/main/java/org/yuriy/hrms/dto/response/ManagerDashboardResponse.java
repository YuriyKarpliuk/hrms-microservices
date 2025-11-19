package org.yuriy.hrms.dto.response;

import java.util.List;

public record ManagerDashboardResponse(
        int totalEmployees,
        int onLeave,
        int pendingTimesheets,
        int approvedPayrolls,
        List<ManagerTopPerformer> topPerformers,
        List<ManagerActivityItem> recentActivity
) {}
