package org.yuriy.leaveservice.dto.response;



public record LeaveSummaryResponse(
        String type,
        long total,
        long used
) {}
