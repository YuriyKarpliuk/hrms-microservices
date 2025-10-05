package org.yuriy.timesheetservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetEventProducer {

    private final KafkaTemplate<String, TimesheetApprovedEvent> kafkaTemplate;

    public void sendTimesheetApproved(TimesheetApprovedEvent event) {
        kafkaTemplate.send("timesheet-events", event.employeeId().toString(), event);
        log.info("TimesheetApprovedEvent sent: {}", event);
    }
}
