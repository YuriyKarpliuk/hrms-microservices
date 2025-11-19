package org.yuriy.hrms.dto.response;

import java.time.LocalDate;

public record LeaveUpcomingResponse(LocalDate startDate, LocalDate endDate, String type) {}
