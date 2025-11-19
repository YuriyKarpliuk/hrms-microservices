package org.yuriy.hrms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yuriy.hrms.dto.request.CreateUserNotificationRequest;
import org.yuriy.hrms.dto.request.HrTaskRequest;
import org.yuriy.hrms.dto.request.HrTaskStatusUpdateRequest;
import org.yuriy.hrms.dto.response.HrTaskResponse;
import org.yuriy.hrms.entity.HrTask;
import org.yuriy.hrms.repository.HrTaskRepository;
import org.yuriy.hrms.service.HrTaskService;
import org.yuriy.hrms.service.NotificationClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HrTaskServiceImpl implements HrTaskService {

    private final HrTaskRepository repo;
    private final NotificationClient notificationClient;
    @Override
    public HrTaskResponse createTask(Long createdBy, HrTaskRequest req) {
        HrTask task = HrTask.builder()
                .employeeId(req.employeeId())
                .createdBy(createdBy)
                .title(req.title())
                .description(req.description())
                .type(req.type())
                .status(HrTask.TaskStatus.PENDING)
                .dueDate(req.dueDate())
                .createdAt(LocalDateTime.now())
                .build();

        repo.save(task);
        return map(task);
    }

    @Override
    public List<HrTaskResponse> getTasksForHr(Long hrId) {
        return repo.findByCreatedByOrderByCreatedAtDesc(hrId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<HrTaskResponse> getTasksForEmployee(Long empId) {
        return repo.findByEmployeeIdOrderByCreatedAtDesc(empId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public HrTaskResponse markCompleted(Long id) {
        HrTask t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        t.setStatus(HrTask.TaskStatus.COMPLETED);
        t.setCompletedAt(LocalDateTime.now());

        return map(repo.save(t));
    }

    @Override
    public HrTaskResponse updateStatus(Long id, HrTaskStatusUpdateRequest req) {
        HrTask t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        t.setStatus(req.status());

        if (req.status() == HrTask.TaskStatus.COMPLETED) {
            t.setCompletedAt(LocalDateTime.now());
        }

        return map(repo.save(t));
    }

    @Override
    public HrTaskResponse getById(Long id) {
        HrTask t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return map(t);
    }

    private HrTaskResponse map(HrTask t) {
        return new HrTaskResponse(
                t.getId(),
                t.getEmployeeId(),
                t.getCreatedBy(),
                t.getTitle(),
                t.getDescription(),
                t.getType(),
                t.getStatus(),
                t.getDueDate(),
                t.getCreatedAt(),
                t.getCompletedAt()
        );
    }
    @Scheduled(cron = "0 0 10 * * *")
    public void sendOverdueNotifications() {

        LocalDate today = LocalDate.now();

        List<HrTask> overdue = repo.findAllByStatusNotAndDueDateBefore(
                HrTask.TaskStatus.COMPLETED,
                today
        );

        overdue.forEach(task -> {

            if (task.isOverdueNotified()) return;

            CreateUserNotificationRequest req = new CreateUserNotificationRequest();
            req.setEmployeeId(task.getEmployeeId());
            req.setSenderId(task.getCreatedBy());
            req.setSenderName("HR System");
            req.setTitle("Task overdue");
            req.setMessage("Your task \"" + task.getTitle() + "\" is overdue.");

            notificationClient.sendNotification(req);

            task.setOverdueNotified(true);
            repo.save(task);
        });
    }
}
