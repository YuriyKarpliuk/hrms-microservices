package org.yuriy.timesheetservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.timesheetservice.dto.mapper.TimesheetMapper;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.EmployeeBasicResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.entity.Timesheet;
import org.yuriy.timesheetservice.entity.ActivityType;
import org.yuriy.timesheetservice.entity.TimesheetEntry;
import org.yuriy.timesheetservice.entity.TimesheetStatus;
import org.yuriy.timesheetservice.kafka.TimesheetApprovedEvent;
import org.yuriy.timesheetservice.kafka.TimesheetEventProducer;
import org.yuriy.timesheetservice.repository.TimesheetRepository;
import org.yuriy.timesheetservice.repository.specification.TimesheetSpecification;
import org.yuriy.timesheetservice.service.EmployeeClient;
import org.yuriy.timesheetservice.service.TimesheetService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional()
@RequiredArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final TimesheetMapper timesheetMapper;
    private final EmployeeClient employeeClient;
    private final TimesheetEventProducer timesheetEventProducer;

    @Override
    public TimesheetResponse createTimesheet(TimesheetCreateRequest req) {
        if (!employeeClient.existsById(req.employeeId())) {
            throw new IllegalArgumentException("Employee with id " + req.employeeId() + " not found");
        }
        Timesheet ts = timesheetMapper.toEntity(req);
        return timesheetMapper.toResponse(timesheetRepository.save(ts));
    }

    @Override
    public List<TimesheetResponse> getTimesheetsByEmployee(Long employeeId) {
        return timesheetRepository.findByEmployeeId(employeeId).stream()
                .map(timesheetMapper::toResponse)
                .toList();
    }

    @Override
    public TimesheetResponse getTimesheetById(Long id) {
        return timesheetRepository.findById(id)
                .map(timesheetMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));
    }

    @Override
    public Page<TimesheetResponse> searchTimesheets(TimesheetSearchRequest request, Pageable pageable) {
        List<Specification<Timesheet>> specifications = new ArrayList<>();

        if (request.employeeId() != null) {
            specifications.add(TimesheetSpecification.hasEmployee(
                    request.employeeId()
            ));
        }

        if (request.startDateFrom() != null || request.startDateTo() != null) {
            specifications.add(TimesheetSpecification.startDateBetween(request.startDateFrom(), request.startDateTo()));
        }

        if (request.endDateFrom() != null || request.endDateTo() != null) {
            specifications.add(TimesheetSpecification.endDateBetween(request.endDateFrom(), request.endDateTo()));
        }


        Specification<Timesheet> specification = Specification.allOf(specifications);

        return timesheetRepository.findAll(specification, pageable)
                .map(timesheetMapper::toResponse);
    }

    @Transactional
    @Override
    public TimesheetResponse approveTimesheet(Long id) {
        Timesheet ts = timesheetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet not found with id " + id));
        ts.setStatus(TimesheetStatus.APPROVED);
        double totalHours = ts.getEntries().stream().mapToDouble(e -> e.getHours() != null ? e.getHours() : 0.0).sum();
        timesheetRepository.save(ts);
        EmployeeBasicResponse employee = employeeClient.getBasicInfo(ts.getEmployeeId());

        timesheetEventProducer.sendTimesheetApproved(new TimesheetApprovedEvent(
                ts.getId(),
                employee.email(),
                ts.getEmployeeId(),
                ts.getWeekStart(),
                ts.getWeekEnd(),
                totalHours
        ));
        return timesheetMapper.toResponse(ts);
    }

    @Transactional
    @Override
    public TimesheetResponse rejectTimesheet(Long id) {
        Timesheet ts = timesheetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet not found with id " + id));
        ts.setStatus(TimesheetStatus.REJECTED);
        return timesheetMapper.toResponse(timesheetRepository.save(ts));
    }

    @Transactional
    public void markLeaveDays(Long employeeId, LocalDate startDate, LocalDate endDate, String type) {
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            LocalDate weekStart = current.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);

            Timesheet timesheet = timesheetRepository
                    .findByEmployeeIdAndWeekStart(employeeId, weekStart)
                    .orElseGet(() -> Timesheet.builder()
                            .employeeId(employeeId)
                            .weekStart(weekStart)
                            .weekEnd(weekEnd)
                            .status(TimesheetStatus.DRAFT)
                            .entries(new ArrayList<>())
                            .build());

            LocalDate finalCurrent = current;
            TimesheetEntry entry = timesheet.getEntries().stream()
                    .filter(e -> e.getWorkDate().equals(finalCurrent))
                    .findFirst()
                    .orElseGet(() -> {
                        TimesheetEntry newEntry = TimesheetEntry.builder()
                                .workDate(finalCurrent)
                                .timesheet(timesheet)
                                .build();
                        timesheet.getEntries().add(newEntry);
                        return newEntry;
                    });

            entry.setActivityType(ActivityType.valueOf(type));
            entry.setHours(0.0);

            log.info("Marked leave {} for employee {} on {}", type, employeeId, current);

            current = current.plusDays(1);
            timesheetRepository.save(timesheet);
        }
    }
}
