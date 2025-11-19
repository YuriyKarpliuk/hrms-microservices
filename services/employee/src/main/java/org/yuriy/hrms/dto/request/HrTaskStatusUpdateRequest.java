package org.yuriy.hrms.dto.request;

import org.yuriy.hrms.entity.HrTask;

public record HrTaskStatusUpdateRequest(
        HrTask.TaskStatus status
) {}
