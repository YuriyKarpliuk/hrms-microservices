package org.yuriy.payrollservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.payrollservice.service.PayrollService;
import org.yuriy.payrollservice.service.impl.PayrollServiceImpl;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEventConsumer {

    private final PayrollService payrollService;

    @KafkaListener(topics = "leave-events", groupId = "payroll-service")
    public void handleDepartmentEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof LeaveApprovedEvent approvedEvent) {
            handleLeaveApproved(approvedEvent);
        } else if (event instanceof LeaveRejectedEvent rejectedEvent) {
            handleLeaveRejected(rejectedEvent);
        } else if (event instanceof LeaveRequestedEvent requestedEvent) {
            handleLeaveRequested(requestedEvent);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    public void handleLeaveApproved(LeaveApprovedEvent event) {
        log.info("LeaveApprovedEvent retrieved: {}", event);
        payrollService.applyLeaveToPayroll(
                event.employeeId(),
                event.startDate(),
                event.endDate(),
                event.type());
    }

    public void handleLeaveRejected(LeaveRejectedEvent event) {
        log.info("LeaveRejectedEvent retrieved: {}", event);
    }

    public void handleLeaveRequested(LeaveRequestedEvent event) {
        log.info("LeaveRequestedEvent retrieved: {}", event);
    }
}
