package org.yuriy.organization.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.organization.service.OrganizationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentEventConsumer {

    @KafkaListener(topics = "department-events", groupId = "organization-service")
    public void handleDepartmentEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof DepartmentCreatedEvent created) {
            handleDepartmentCreated(created);
        } else if (event instanceof DepartmentUpdatedEvent updated) {
            handleDepartmentUpdated(updated);
        } else if (event instanceof DepartmentDeletedEvent deleted) {
            handleDepartmentDeleted(deleted);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    public void handleDepartmentCreated(DepartmentCreatedEvent event) {
        log.info("DepartmentCreatedEvent retrieved: {}", event);
    }

    public void handleDepartmentUpdated(DepartmentUpdatedEvent event) {
        log.info("DepartmentUpdatedEvent retrieved: {}", event);
    }

    public void handleDepartmentDeleted(DepartmentDeletedEvent event) {
        log.info("DepartmentDeletedEvent retrieved: {}", event);
    }

}
