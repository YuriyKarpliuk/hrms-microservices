package org.yuriy.hrms.dto.response;

import org.yuriy.hrms.entity.HrTask;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HrTaskResponse(
        Long id,
        Long employeeId,
        Long createdBy,
        String title,
        String description,
        HrTask.TaskType type,
        HrTask.TaskStatus status,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
