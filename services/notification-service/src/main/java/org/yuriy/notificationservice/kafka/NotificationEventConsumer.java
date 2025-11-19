package org.yuriy.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.entity.NotificationType;
import org.yuriy.notificationservice.entity.UserNotification;
import org.yuriy.notificationservice.repository.UserNotificationRepository;
import org.yuriy.notificationservice.service.EmailService;
import org.yuriy.notificationservice.service.WebSocketNotificationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final EmailService emailService;
    private final UserNotificationRepository userNotificationRepository;
    private final WebSocketNotificationService wsService;

    @KafkaListener(topics = "employee-events", groupId = "notification-service")
    public void handleEmployeeEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof EmployeeCreatedEvent created) {
            handleEmployeeCreated(created);
        } else if (event instanceof EmployeeUpdatedEvent updated) {
            handleEmployeeUpdated(updated);
        } else if (event instanceof EmployeeDeletedEvent deleted) {
            handleEmployeeDeleted(deleted);
        } else {
            log.warn("Unknown employee event: {}", consumerRecord);
        }
    }

    private void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info(" EmployeeCreatedEvent received: {}", event);

        emailService.sendEmail(
                event.email(),
                "Welcome to the company ",
                "Hello " + event.firstName() + ", your employee account has been successfully created."
        );

        UserNotification notification = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.id())
                .title("Welcome aboard ")
                .message("Your employee account has been successfully created.")
                .type(NotificationType.SYSTEM)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.id(), notification);
    }

    private void handleEmployeeUpdated(EmployeeUpdatedEvent event) {
        log.info(" EmployeeUpdatedEvent received: {}", event);
    }

    private void handleEmployeeDeleted(EmployeeDeletedEvent event) {
        log.info(" EmployeeDeletedEvent received: {}", event);
    }


    @KafkaListener(topics = "leave-events", groupId = "notification-service")
    public void handleLeaveEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof LeaveApprovedEvent approved) {
            handleLeaveApproved(approved);
        } else if (event instanceof LeaveRejectedEvent rejected) {
            handleLeaveRejected(rejected);
        } else if (event instanceof LeaveRequestedEvent requested) {
            handleLeaveRequested(requested);
        } else {
            log.warn(" Unknown leave event: {}", consumerRecord);
        }
    }

    private void handleLeaveApproved(LeaveApprovedEvent event) {
        log.info(" LeaveApprovedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Your leave has been approved ",
                String.format("Your leave from %s to %s (%s) has been approved. Enjoy your time off!",
                        event.startDate(), event.endDate(), event.type())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Leave Approved ")
                .message(String.format("Leave from %s to %s (%s) approved.", event.startDate(), event.endDate(), event.type()))
                .type(NotificationType.LEAVE)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }

    private void handleLeaveRejected(LeaveRejectedEvent event) {
        log.info(" LeaveRejectedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Leave Rejected ",
                String.format("Unfortunately, your leave from %s to %s was rejected. Please contact your manager.",
                        event.startDate(), event.endDate())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Leave Rejected ")
                .message(String.format("Your leave from %s to %s was rejected.", event.startDate(), event.endDate()))
                .type(NotificationType.LEAVE)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }

    private void handleLeaveRequested(LeaveRequestedEvent event) {
        log.info(" LeaveRequestedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Leave Request Submitted ",
                String.format("Your leave request from %s to %s (%s) has been submitted for approval.",
                        event.from(), event.to(), event.type())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Leave Requested ")
                .message(String.format("Your leave from %s to %s (%s) is pending approval.",
                        event.from(), event.to(), event.type()))
                .type(NotificationType.LEAVE)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }


    @KafkaListener(topics = "timesheet-events", groupId = "notification-service")
    public void handleTimesheetEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof TimesheetApprovedEvent approved) {
            handleTimesheetApproved(approved);
        } else {
            log.warn(" Unknown timesheet event: {}", consumerRecord);
        }
    }

    private void handleTimesheetApproved(TimesheetApprovedEvent event) {
        log.info(" TimesheetApprovedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Timesheet Approved ",
                String.format("Your timesheet for %s - %s was approved. Total hours: %.2f",
                        event.weekStart(), event.weekEnd(), event.totalHours())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Timesheet Approved")
                .message(String.format("Week %s - %s approved. %.1f hours logged.",
                        event.weekStart(), event.weekEnd(), event.totalHours()))
                .type(NotificationType.TIMESHEET)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }


    @KafkaListener(topics = "payroll-events", groupId = "notification-service")
    public void handlePayrollEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof PayrollCreatedEvent created) {
            handlePayrollCreated(created);
        } else if (event instanceof PayrollPayedEvent paid) {
            handlePayrollPayed(paid);
        } else if (event instanceof PayrollFailedEvent failed) {
            handlePayrollFailed(failed);
        } else {
            log.warn(" Unknown payroll event: {}", consumerRecord);
        }
    }

    private void handlePayrollCreated(PayrollCreatedEvent event) {
        log.info(" PayrollCreatedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Payroll Created",
                String.format("Payroll created for %s - %s. Status: %s",
                        event.periodStart(), event.periodEnd(), event.status())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Payroll Created ")
                .message(String.format("New payroll for %s - %s (status: %s).",
                        event.periodStart(), event.periodEnd(), event.status()))
                .type(NotificationType.PAYROLL)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }

    private void handlePayrollPayed(PayrollPayedEvent event) {
        log.info("💸 PayrollPayedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Payroll Paid ",
                String.format("Your payroll for %s - %s has been successfully processed.",
                        event.periodStart(), event.periodEnd())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Payroll Paid ")
                .message(String.format("Your payroll for %s - %s was paid.",
                        event.periodStart(), event.periodEnd()))
                .type(NotificationType.PAYROLL)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }

    private void handlePayrollFailed(PayrollFailedEvent event) {
        log.info(" PayrollFailedEvent: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Payroll Failed ",
                String.format("Error processing payroll for %s - %s. Please contact HR.",
                        event.periodStart(), event.periodEnd())
        );

        UserNotification n = userNotificationRepository.save(UserNotification.builder()
                .employeeId(event.employeeId())
                .title("Payroll Failed")
                .message(String.format("Payroll for %s - %s failed. Contact HR.",
                        event.periodStart(), event.periodEnd()))
                .type(NotificationType.PAYROLL)
                .createdAt(LocalDateTime.now())
                .build());

        wsService.sendToUser(event.employeeId(), n);
    }
}
