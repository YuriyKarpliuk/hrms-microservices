package org.yuriy.leaveservice.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovedLeaveDto {

    private LocalDate date;

    private String type;
}
