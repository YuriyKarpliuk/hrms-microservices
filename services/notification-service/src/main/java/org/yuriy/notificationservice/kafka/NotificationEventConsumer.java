package org.yuriy.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.dto.response.EmployeeBasicResponse;
import org.yuriy.notificationservice.service.EmailService;
import org.yuriy.notificationservice.service.EmployeeClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final EmailService emailService;

    private final EmployeeClient employeeClient;

    @KafkaListener(topics = "employee-events", groupId = "notification-service")
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("EmployeeCreatedEvent retrieved: {}", event);
        emailService.sendEmail(event.email(), "Welcome to the company!",
                "Hello " + event.firstName() + ", your profile has been created.");
    }

    @KafkaListener(topics = "leave-events", groupId = "notification-service")
    public void handleLeaveApproved(LeaveApprovedEvent event) {
        log.info("LeaveApprovedEvent retrieved: {}", event);
        EmployeeBasicResponse employee = employeeClient.getBasicInfo(event.employeeId());

        emailService.sendEmail(
                employee.email(),
                "Your leave request has been approved",
                String.format(
                        "Hello,\n\nYour leave from %s to %s (%s) has been approved.\n\nEnjoy your time off!",
                        event.startDate(), event.endDate(), event.type()
                )
        );
    }

    @KafkaListener(topics = "leave-events", groupId = "notification-service")
    public void handleLeaveRejected(LeaveRejectedEvent event) {
        log.info("LeaveRejectedEvent retrieved: {}", event);

        EmployeeBasicResponse employee = employeeClient.getBasicInfo(event.employeeId());

        emailService.sendEmail(
                employee.email(),
                "Your leave has been rejected",
                String.format(
                        "Hello %s,\n\nUnfortunately, your leave request from %s to %s has been rejected.\n\nPlease contact your manager.",
                        employee.firstName(),
                        event.startDate(),
                        event.endDate()
                )
        );
    }

    @KafkaListener(topics = "timesheet-events", groupId = "notification-service")
    public void handleTimesheetApproved(TimesheetApprovedEvent event) {
        log.info("TimesheetApprovedEvent retrieved: {}", event);
        EmployeeBasicResponse employee = employeeClient.getBasicInfo(event.employeeId());

        emailService.sendEmail(
                employee.email(),
                "Timesheet approved",
                String.format(
                        "Hello,\n\nYour timesheet for the week %s - %s has been approved. Total logged hours: %.2f.\n\nThank you for submitting on time.",
                        event.weekStart(), event.weekEnd(), event.totalHours()
                )
        );
    }
}
