package org.yuriy.department.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventConsumer {
    @KafkaListener(topics = "employee-events", groupId = "department-service")
    public void handleEmployeeEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof EmployeeCreatedEvent created) {
            handleEmployeeCreated(created);
        } else if (event instanceof EmployeeUpdatedEvent updated) {
            handleEmployeeUpdated(updated);
        } else if (event instanceof EmployeeDeletedEvent deleted) {
            handleEmployeeDeleted(deleted);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("EmployeeCreatedEvent retrieved: {}", event);
    }

    public void handleEmployeeUpdated(EmployeeUpdatedEvent event) {
        log.info("EmployeeUpdatedEvent retrieved: {}", event);
    }

    public void handleEmployeeDeleted(EmployeeDeletedEvent event) {
        log.info("EmployeeDeletedEvent retrieved: {}", event);
    }

}
