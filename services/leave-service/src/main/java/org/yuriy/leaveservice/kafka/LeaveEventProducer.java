package org.yuriy.leaveservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEventProducer {

    private final KafkaTemplate<String, LeaveRequestedEvent> requestedTemplate;
    private final KafkaTemplate<String, LeaveApprovedEvent> approvedTemplate;
    private final KafkaTemplate<String, LeaveRejectedEvent> rejectedTemplate;

    public void sendLeaveRequested(LeaveRequestedEvent event) {
        requestedTemplate.send("leave-events", event.employeeId().toString(), event);
        log.info("LeaveRequestedEvent sent: {}", event);
    }

    public void sendLeaveApproved(LeaveApprovedEvent event) {
        approvedTemplate.send("leave-events", event.employeeId().toString(), event);
        log.info("LeaveApprovedEvent sent: {}", event);
    }

    public void sendLeaveRejected(LeaveRejectedEvent event) {
        rejectedTemplate.send("leave-events", event.employeeId().toString(), event);
        log.info("LeaveRejectedEvent sent: {}", event);
    }
}
