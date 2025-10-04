package org.yuriy.organization.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.organization.service.OrganizationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentEventConsumer {

    private final OrganizationService organizationService;

    @KafkaListener(topics = "department-events", groupId = "organization-service")
    public void handleDepartmentCreated(DepartmentCreatedEvent event) {
        log.info("DepartmentCreatedEvent retrieved: {}", event);
    }

    @KafkaListener(topics = "department-events", groupId = "organization-service")
    public void handleDepartmentCreated(DepartmentUpdatedEvent event) {
        log.info("DepartmentUpdatedEvent retrieved: {}", event);
    }

    @KafkaListener(topics = "department-events", groupId = "organization-service")
    public void handleDepartmentCreated(DepartmentDeletedEvent event) {
        log.info("DepartmentDeletedEvent retrieved: {}", event);
    }

}
