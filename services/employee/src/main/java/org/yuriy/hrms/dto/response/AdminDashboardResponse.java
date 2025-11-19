package org.yuriy.hrms.dto.response;

import java.util.List;

public record AdminDashboardResponse(
        Metrics metrics,
        List<OrgCount> topOrganizations,
        List<DeptCount> topDepartments,
        GenderStats gender,
        int newEmployeesThisMonth,
        List<Integer> roleStats,
        NewAccounts newAccounts
) {

    public record Metrics(
            long employees,
            long organizations,
            long departments
    ) {}

    public record OrgCount(String name, long count) {}
    public record DeptCount(String name, long count) {}
    public record NewAccounts(
            List<String> days,
            List<Integer> values
    ) {}

    public record GenderStats(long male, long female) {}
}
