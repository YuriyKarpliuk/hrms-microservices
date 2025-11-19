package org.yuriy.hrms.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yuriy.hrms.dto.response.HrDashboardResponse;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.entity.HrTask;
import org.yuriy.hrms.repository.EmployeeRepository;
import org.yuriy.hrms.repository.HrTaskRepository;
import org.yuriy.hrms.service.HrDashboardService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrDashboardServiceImpl implements HrDashboardService {

    private final EmployeeRepository employeeRepository;
    private final HrTaskRepository taskRepository;

    public HrDashboardResponse getDashboard(Long hrId) {

        Employee hr = employeeRepository.findById(hrId)
                .orElseThrow(() -> new IllegalArgumentException("HR not found"));

        Long orgId = hr.getOrgId();

        int totalEmployees = employeeRepository.countByOrgIdAndStatus(orgId, Employee.Status.ACTIVE);
        int openTasks = taskRepository.countByOrgIdAndStatus(orgId, HrTask.TaskStatus.PENDING);
        int newHires = employeeRepository.countHiredThisMonth(orgId);
        int attrition = calculateAttrition(orgId);

        List<HrDashboardResponse.TaskItem> tasks =
                taskRepository.findTopTasks(
                                orgId, HrTask.TaskStatus.PENDING)
                        .stream()
                        .map(t -> HrDashboardResponse.TaskItem.builder()
                                .id(t.getId())
                                .title(t.getTitle())
                                .employeeName(
                                        employeeRepository.findById(t.getEmployeeId())
                                                .map(e -> e.getFirstName() + " " + e.getLastName())
                                                .orElse("Unknown")
                                )
                                .type(t.getType().name())
                                .dueDate(t.getDueDate())
                                .build()
                        ).toList();

        LocalDate now = LocalDate.now();
        List<HrDashboardResponse.BirthdayItem> birthdays =
                employeeRepository.findByOrgId(orgId)
                        .stream()
                        .filter(e -> e.getBirthDate() != null)
                        .filter(e ->
                                ChronoUnit.DAYS.between(now, e.getBirthDate().withYear(now.getYear())) >= 0 &&
                                        ChronoUnit.DAYS.between(now, e.getBirthDate().withYear(now.getYear())) <= 30
                        )
                        .map(e -> HrDashboardResponse.BirthdayItem.builder()
                                .id(e.getId())
                                .name(e.getFirstName() + " " + e.getLastName())
                                .date(e.getBirthDate().withYear(now.getYear()))
                                .build()
                        )
                        .toList();
        List<Integer> recruitStats = List.of(12, 5, 2, 1);

        int pending = taskRepository.countByOrgIdAndTypeAndStatus(
                orgId, HrTask.TaskType.ONBOARDING, HrTask.TaskStatus.PENDING);
        int inProgress = taskRepository.countByOrgIdAndTypeAndStatus(
                orgId, HrTask.TaskType.ONBOARDING, HrTask.TaskStatus.IN_PROGRESS);
        int completed = taskRepository.countByOrgIdAndTypeAndStatus(
                orgId, HrTask.TaskType.ONBOARDING, HrTask.TaskStatus.COMPLETED);

        List<Integer> onboardStats = List.of(pending, inProgress, completed);

        return HrDashboardResponse.builder()
                .metrics(
                        HrDashboardResponse.Metrics.builder()
                                .totalEmployees(totalEmployees)
                                .openTasks(openTasks)
                                .newHires(newHires)
                                .attrition(attrition)
                                .build()
                )
                .topTasks(tasks)
                .birthdays(birthdays)
                .recruitStats(recruitStats)
                .onboardStats(onboardStats)
                .build();
    }

    private int calculateAttrition(Long orgId) {
        int total = employeeRepository.countByOrgId(orgId);
        int terminated = employeeRepository.countByOrgIdAndStatus(orgId, Employee.Status.TERMINATED);
        if (total == 0) return 0;
        return (int) ((terminated * 100.0) / total);
    }

}
