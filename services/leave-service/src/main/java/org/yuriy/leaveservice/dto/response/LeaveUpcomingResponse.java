package org.yuriy.leaveservice.dto.response;

import java.time.LocalDate;

public record LeaveUpcomingResponse(
        LocalDate startDate,
        LocalDate endDate,
        String type
) {}
