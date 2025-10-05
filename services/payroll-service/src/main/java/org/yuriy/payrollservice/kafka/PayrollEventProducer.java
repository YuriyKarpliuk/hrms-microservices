package org.yuriy.payrollservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollEventProducer {

    private final KafkaTemplate<String, PayrollCreatedEvent> createdTemplate;
    private final KafkaTemplate<String, PayrollPayedEvent> payedTemplate;
    private final KafkaTemplate<String, PayrollFailedEvent> failedTemplate;

    public void sendPayrollCreated(PayrollCreatedEvent event) {
        createdTemplate.send("payroll-events", event.employeeId().toString(), event);
        log.info("PayrollCreatedEvent sent: {}", event);
    }

    public void sendPayrollPayed(PayrollPayedEvent event) {
        payedTemplate.send("payroll-events", event.employeeEmail(), event);
        log.info("PayrollPayedEvent sent: {}", event);
    }

    public void sendPayrollFailed(PayrollFailedEvent event) {
        failedTemplate.send("payroll-events", event.employeeId().toString(), event);
        log.info("PayrollFailedEvent sent: {}", event);
    }
}
