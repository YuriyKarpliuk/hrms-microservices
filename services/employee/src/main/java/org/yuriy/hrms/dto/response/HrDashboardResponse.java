package org.yuriy.hrms.dto.response;


import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record HrDashboardResponse(
        Metrics metrics,
        List<TaskItem> topTasks,
        List<BirthdayItem> birthdays,
        List<Integer> recruitStats,
        List<Integer> onboardStats
) {
    @Builder
    public  record Metrics(
            int totalEmployees,
            int openTasks,
            int newHires,
            int attrition
    ) {}

    @Builder
    public  record TaskItem(
            Long id,
            String title,
            String employeeName,
            String type,
            LocalDate dueDate
    ) {}

    @Builder
    public  record BirthdayItem(
            Long id,
            String name,
            LocalDate date
    ) {}
}
