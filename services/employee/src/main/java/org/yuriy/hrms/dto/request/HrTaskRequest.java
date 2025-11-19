package org.yuriy.hrms.dto.request;

import org.yuriy.hrms.entity.HrTask;

import java.time.LocalDate;

public record HrTaskRequest(
        Long employeeId,
        Long hrId,
        String title,
        String description,
        HrTask.TaskType type,
        LocalDate dueDate
) {}

