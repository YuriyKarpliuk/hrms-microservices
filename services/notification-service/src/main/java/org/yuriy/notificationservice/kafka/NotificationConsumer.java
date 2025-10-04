package org.yuriy.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "employee-events", groupId = "notification-service")
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("EmployeeCreatedEvent retrieved: {}", event);
        emailService.sendEmail(event.email(), "Welcome to the company!",
                "Hello " + event.firstName() + ", your profile has been created.");
    }

    //    @KafkaListener(topics = "leave-events", groupId = "notification-service")
    //    public void handleLeaveApproved(LeaveApprovedEvent event) {
    //        String employeeEmail = getEmailByEmployeeId(event.employeeId());
    //        emailService.sendEmail(
    //                employeeEmail,
    //                "Leave Approved",
    //                "Your leave from " + event.startDate() + " to " + event.endDate() + " has been approved."
    //        );
    //    }
    //
    //    @KafkaListener(topics = "leave-events", groupId = "notification-service")
    //    public void handleLeaveRejected(LeaveRejectedEvent event) {
    //        String employeeEmail = getEmailByEmployeeId(event.employeeId());
    //        emailService.sendEmail(
    //                employeeEmail,
    //                "Leave Rejected",
    //                "Your leave request was rejected."
    //        );
    //    }
    //
    //    @KafkaListener(topics = "timesheet-events", groupId = "notification-service")
    //    public void handleTimesheetApproved(TimesheetApprovedEvent event) {
    //        String employeeEmail = getEmailByEmployeeId(event.employeeId());
    //        emailService.sendEmail(
    //                employeeEmail,
    //                "Timesheet Approved",
    //                "Your timesheet for " + event.weekStart() + " - " + event.weekEnd() + " has been approved."
    //        );
    //    }

}
