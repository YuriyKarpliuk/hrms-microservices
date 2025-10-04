package org.yuriy.timesheetservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.timesheetservice.service.TimesheetService;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEventConsumer {
    private final TimesheetService timesheetService;

    @KafkaListener(topics = "leave-events", groupId = "timesheet-service")
    public void handleLeaveApproved(LeaveApprovedEvent event) {
        log.info("Processing LeaveApprovedEvent: {}", event);
        timesheetService.markLeaveDays(
                event.employeeId(),
                event.startDate(),
                event.endDate(),
                event.type()
        );
    }
}
