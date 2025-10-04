package org.yuriy.hrms.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventProducer {

    private final KafkaTemplate<String, EmployeeCreatedEvent> createdTemplate;
    private final KafkaTemplate<String, EmployeeUpdatedEvent> updatedTemplate;
    private final KafkaTemplate<String, EmployeeDeletedEvent> deletedTemplate;

    public void sendEmployeeCreated(EmployeeCreatedEvent event) {
        createdTemplate.send("employee-events", event.id().toString(), event);
        log.info("EmployeeCreatedEvent sent: {}", event);
    }

    public void sendEmployeeUpdated(EmployeeUpdatedEvent event) {
        updatedTemplate.send("employee-events", event.id().toString(), event);
        log.info("EmployeeUpdatedEvent sent: {}", event);
    }

    public void sendEmployeeDeleted(EmployeeDeletedEvent event) {
        deletedTemplate.send("employee-events", event.id().toString(), event);
        log.info("EmployeeDeletedEvent sent: {}", event);
    }
}
