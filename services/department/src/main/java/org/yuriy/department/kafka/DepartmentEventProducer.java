package org.yuriy.department.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentEventProducer {

    private final KafkaTemplate<String, DepartmentCreatedEvent> createdTemplate;
    private final KafkaTemplate<String, DepartmentUpdatedEvent> updatedTemplate;
    private final KafkaTemplate<String, DepartmentDeletedEvent> deletedTemplate;

    public void sendDepartmentCreated(DepartmentCreatedEvent event) {
        createdTemplate.send("department-events", event.id().toString(), event);
        log.info("DepartmentCreatedEvent sent: {}", event);
    }

    public void sendDepartmentUpdated(DepartmentUpdatedEvent event) {
        updatedTemplate.send("department-events", event.id().toString(), event);
        log.info("DepartmentUpdatedEvent sent: {}", event);
    }

    public void sendDepartmentDeleted(DepartmentDeletedEvent event) {
        deletedTemplate.send("department-events", event.id().toString(), event);
        log.info("DepartmentDeletedEvent sent: {}", event);
    }
}
