package org.yuriy.leaveservice.dto.request;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record LeaveCreateRequest(@NotNull Long employeeId,
        @NotNull String type,
        @NotNull @FutureOrPresent(message = "Start date must be today or in the future") LocalDate startDate,
        @NotNull @Future(message = "End date must be in the future") LocalDate endDate,
        String reason) {
}

