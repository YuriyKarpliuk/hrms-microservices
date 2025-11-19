package org.yuriy.hrms.dto.response;

import java.time.LocalDateTime;

public record ManagerActivityItem(
        String type,
        String description,
        LocalDateTime timestamp
) {}
