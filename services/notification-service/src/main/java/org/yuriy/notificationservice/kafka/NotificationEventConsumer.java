package org.yuriy.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yuriy.notificationservice.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "employee-events", groupId = "notification-service")
    public void handleEmployeeEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof EmployeeCreatedEvent created) {
            handleEmployeeCreated(created);
        } else if (event instanceof EmployeeUpdatedEvent updateEvent) {
            handleEmployeeUpdated(updateEvent);
        } else if (event instanceof EmployeeDeletedEvent employeeDeletedEvent) {
            handleEmployeeDeleted(employeeDeletedEvent);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    private void handleEmployeeDeleted(EmployeeDeletedEvent employeeDeletedEvent) {
        log.info("EmployeeDeletedEvent retrieved: {}", employeeDeletedEvent);
    }

    private void handleEmployeeUpdated(EmployeeUpdatedEvent updateEvent) {
        log.info("EmployeeUpdatedEvent retrieved: {}", updateEvent);
    }

    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("EmployeeCreatedEvent retrieved: {}", event);
        emailService.sendEmail(event.email(), "Welcome to the company!",
                "Hello " + event.firstName() + ", your profile has been created.");
    }

    @KafkaListener(topics = "leave-events", groupId = "notification-service")
    public void handleLeaveEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof LeaveApprovedEvent approvedEvent) {
            handleLeaveApproved(approvedEvent);
        } else if (event instanceof LeaveRejectedEvent leaveRejectedEvent) {
            handleLeaveRejected(leaveRejectedEvent);
        } else if (event instanceof LeaveRequestedEvent leaveRequestedEvent) {
            handleLeaveRequested(leaveRequestedEvent);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    public void handleLeaveApproved(LeaveApprovedEvent event) {
        log.info("LeaveApprovedEvent retrieved: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Your leave request has been approved",
                String.format(
                        "Hello,\n\nYour leave from %s to %s (%s) has been approved.\n\nEnjoy your time off!",
                        event.startDate(), event.endDate(), event.type()
                )
        );
    }

    public void handleLeaveRejected(LeaveRejectedEvent event) {
        log.info("LeaveRejectedEvent retrieved: {}", event);


        emailService.sendEmail(
                event.employeeEmail(),
                "Your leave has been rejected",
                String.format(
                        "Unfortunately, your leave request from %s to %s has been rejected.\n\nPlease contact your manager.",
                        event.startDate(),
                        event.endDate()
                )
        );
    }

    public void handleLeaveRequested(LeaveRequestedEvent event) {
        log.info("LeaveRequestedEvent retrieved: {}", event);


        emailService.sendEmail(
                event.employeeEmail(),
                "Your leave request has been submitted",
                String.format(
                        "Your leave request from %s to %s (%s) has been successfully submitted and is pending approval.\n\nYou will be notified once your manager reviews it.\n\nBest regards,\nHR Team",
                        event.from(),
                        event.to(),
                        event.type()
                )
        );
    }

    @KafkaListener(topics = "timesheet-events", groupId = "notification-service")
    public void handleTimesheetEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof TimesheetApprovedEvent timesheetApprovedEvent) {
            handleTimesheetApproved(timesheetApprovedEvent);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    public void handleTimesheetApproved(TimesheetApprovedEvent event) {
        log.info("TimesheetApprovedEvent retrieved: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Timesheet approved",
                String.format(
                        "Hello,\n\nYour timesheet for the week %s - %s has been approved. Total logged hours: %.2f.\n\nThank you for submitting on time.",
                        event.weekStart(), event.weekEnd(), event.totalHours()
                )
        );
    }

    @KafkaListener(topics = "payroll-events", groupId = "notification-service")
    public void handlePayrollEvent(ConsumerRecord<String, Object> consumerRecord) {
        Object event = consumerRecord.value();

        if (event instanceof PayrollCreatedEvent createdEvent) {
            handlePayrollCreated(createdEvent);
        } else if (event instanceof PayrollFailedEvent failedEvent) {
            handlePayrollFailed(failedEvent);
        } else if (event instanceof PayrollPayedEvent payrollPayedEvent) {
            handlePayrollPayed(payrollPayedEvent);
        } else {
            log.warn("Received unknown event: {}", consumerRecord);
        }
    }

    private void handlePayrollCreated(PayrollCreatedEvent event) {
        log.info("PayrollCreatedEvent retrieved: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Payroll created",
                String.format(
                        "Hello,\n\nA new payroll record has been created for the period %s - %s.\n" +
                                "Current status: %s.\n\nYou will receive a notification once the payment is processed.\n\nBest regards,\nHR & Payroll Team",
                        event.periodStart(),
                        event.periodEnd(),
                        event.status()
                )
        );
    }

    private void handlePayrollPayed(PayrollPayedEvent event) {
        log.info("PayrollPayedEvent retrieved: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Your payroll has been paid",
                String.format(
                        "Hello,\n\nYour payroll for the period %s - %s has been successfully processed and paid.\n\nBest regards,\nHR & Payroll Team",
                        event.periodStart(),
                        event.periodEnd()
                )
        );
    }

    private void handlePayrollFailed(PayrollFailedEvent event) {
        log.info("PayrollFailedEvent retrieved: {}", event);

        emailService.sendEmail(
                event.employeeEmail(),
                "Payroll processing failed",
                String.format(
                        "Hello,\n\nWe encountered an issue while processing your payroll for the period %s - %s.\n" +
                                "Please contact the payroll department for further assistance.\n\nBest regards,\nHR & Payroll Team",
                        event.periodStart(),
                        event.periodEnd()
                )
        );
    }

}
