package org.yuriy.leaveservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.leaveservice.dto.mapper.LeaveMapper;
import org.yuriy.leaveservice.dto.request.LeaveCreateRequest;
import org.yuriy.leaveservice.dto.request.LeaveSearchRequest;
import org.yuriy.leaveservice.dto.response.EmployeeBasicResponse;
import org.yuriy.leaveservice.dto.response.LeaveResponse;
import org.yuriy.leaveservice.dto.response.LeaveSummaryResponse;
import org.yuriy.leaveservice.dto.response.LeaveUpcomingResponse;
import org.yuriy.leaveservice.entity.Leave;
import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;
import org.yuriy.leaveservice.kafka.LeaveApprovedEvent;
import org.yuriy.leaveservice.kafka.LeaveEventProducer;
import org.yuriy.leaveservice.kafka.LeaveRejectedEvent;
import org.yuriy.leaveservice.kafka.LeaveRequestedEvent;
import org.yuriy.leaveservice.repository.LeaveRepository;
import org.yuriy.leaveservice.repository.specification.LeaveSpecification;
import org.yuriy.leaveservice.service.EmployeeClient;
import org.yuriy.leaveservice.service.LeaveService;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveMapper leaveMapper;
    private final EmployeeClient employeeClient;
    private final LeaveEventProducer leaveEventProducer;

    @Transactional
    @Override
    public LeaveResponse requestLeave(LeaveCreateRequest req) {
        if (!employeeClient.existsById(req.employeeId())) {
            throw new IllegalArgumentException("Employee with id " + req.employeeId() + " not found");
        }
        if (req.endDate().isBefore(req.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        Leave leave = leaveMapper.toEntity(req);
        leaveRepository.save(leave);
        EmployeeBasicResponse employeeBasicResponse = employeeClient.getBasicInfo(leave.getEmployeeId());

        leaveEventProducer.sendLeaveRequested(new LeaveRequestedEvent(
                leave.getId(),
                employeeBasicResponse.email(),
                leave.getEmployeeId(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getType().name(),
                leave.getReason()));
        return leaveMapper.toResponse(leave);
    }

    @Override
    public List<LeaveResponse> getLeavesByEmployee(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId)
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LeaveResponse approveLeave(Long leaveId, Long managerId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(LeaveStatus.APPROVED);
        leaveRepository.save(leave);
        EmployeeBasicResponse employeeBasicResponse = employeeClient.getBasicInfo(leave.getEmployeeId());
        leaveEventProducer.sendLeaveApproved(new LeaveApprovedEvent(
                leave.getId(),
                employeeBasicResponse.email(),
                leave.getEmployeeId(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getType().name(),
                managerId));

        return leaveMapper.toResponse(leave);
    }

    @Override
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId, Long managerId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(LeaveStatus.REJECTED);
        leaveRepository.save(leave);
        EmployeeBasicResponse employeeBasicResponse = employeeClient.getBasicInfo(leave.getEmployeeId());

        leaveEventProducer.sendLeaveRejected(new LeaveRejectedEvent(
                leave.getId(),
                employeeBasicResponse.email(),
                leave.getEmployeeId(),
                managerId,
                leave.getStartDate(),
                leave.getEndDate()
        ));
        return leaveMapper.toResponse(leave);
    }

    @Override
    public Page<LeaveResponse> searchLeaves(LeaveSearchRequest request, Pageable pageable) {
        List<Specification<Leave>> specifications = new ArrayList<>();

        if (request.employeeId() != null) {
            specifications.add(LeaveSpecification.hasEmployeeId(
                    request.employeeId()
            ));
        }
        if (request.status() != null) {
            specifications.add(LeaveSpecification.hasStatus(request.status()));
        }
        if (request.type() != null) {
            specifications.add(LeaveSpecification.hasType(request.type()));
        }
        if (request.startFrom() != null || request.startTo() != null) {
            specifications.add(LeaveSpecification.startDateBetween(request.startFrom(), request.startTo()));
        }
        if (request.endFrom() != null || request.endTo() != null) {
            specifications.add(LeaveSpecification.endDateBetween(request.endFrom(), request.endTo()));
        }

        Specification<Leave> specification = Specification.allOf(specifications);

        return leaveRepository.findAll(specification, pageable)
                .map(leaveMapper::toResponse);
    }

    @Override
    public List<LeaveSummaryResponse> getLeaveSummary(Long employeeId) {
        Map<LeaveType, Long> usedDays = leaveRepository.findByEmployeeId(employeeId).stream()
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED)
                .collect(Collectors.groupingBy(
                        Leave::getType,
                        Collectors.summingLong(l ->
                                ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                ));

        Map<LeaveType, Long> totalDays = Map.of(
                LeaveType.VACATION, 20L,
                LeaveType.SICK, 10L,
                LeaveType.UNPAID, 10L
        );

        return Arrays.stream(LeaveType.values())
                .map(type -> {
                    long total = totalDays.getOrDefault(type, 0L);
                    long used = usedDays.getOrDefault(type, 0L);
                    return new LeaveSummaryResponse(
                            type.name(),
                            total,
                            used
                    );
                })
                .sorted(Comparator.comparing(LeaveSummaryResponse::type)) // для стабільного порядку
                .toList();
    }



    @Override
    public Double getRemainingDays(Long employeeId) {
        int total = 20;
        int used = leaveRepository.countUsedVacationDays(employeeId, Year.now().getValue());
        return (double) (total - used);
    }

    @Override
    public List<LeaveUpcomingResponse> getUpcomingLeaves(Long employeeId) {
        LocalDate today = LocalDate.now();
        return leaveRepository.findByEmployeeIdAndStartDateAfter(employeeId, today)
                .stream()
                .map(l -> new LeaveUpcomingResponse(l.getStartDate(), l.getEndDate(), l.getType().name()))
                .toList();
    }

    @Override
    public Page<LeaveResponse> searchLeavesForManager(LeaveSearchRequest request, Pageable pageable) {
        List<EmployeeBasicResponse> team = employeeClient.getEmployeesByManager(request.managerId());

        if (request.employeeName() != null && !request.employeeName().isBlank()) {
            String query = request.employeeName().toLowerCase();
            team = team.stream()
                    .filter(e -> (e.firstName() + " " + e.lastName()).toLowerCase().contains(query))
                    .toList();
        }

        List<Long> employeeIds = team.stream()
                .map(EmployeeBasicResponse::id)
                .toList();

        if (employeeIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Specification<Leave>> specs = new ArrayList<>();
        specs.add(LeaveSpecification.employeeIn(employeeIds));

        if (request.status() != null) specs.add(LeaveSpecification.hasStatus(request.status()));
        if (request.type() != null) specs.add(LeaveSpecification.hasType(request.type()));
        if (request.startFrom() != null)
            specs.add(LeaveSpecification.startAfterOrEqual(request.startFrom()));
        if (request.endTo() != null)
            specs.add(LeaveSpecification.endBeforeOrEqual(request.endTo()));
        Specification<Leave> spec = Specification.allOf(specs);

        return leaveRepository.findAll(spec, pageable)
                .map(leaveMapper::toResponse);
    }



}
