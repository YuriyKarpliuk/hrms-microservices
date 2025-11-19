package org.yuriy.hrms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hr_tasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HrTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private Long createdBy;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate dueDate;
    @Column(nullable = false)
    private boolean overdueNotified = false;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    public enum TaskType {
        ONBOARDING,
        OFFBOARDING,
        REVIEW,
        LEAVE_APPROVAL,
        CUSTOM
    }

    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

}

