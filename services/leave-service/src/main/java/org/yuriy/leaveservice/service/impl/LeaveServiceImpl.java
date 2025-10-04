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
import org.yuriy.leaveservice.dto.response.LeaveResponse;
import org.yuriy.leaveservice.entity.Leave;
import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.kafka.LeaveApprovedEvent;
import org.yuriy.leaveservice.kafka.LeaveEventProducer;
import org.yuriy.leaveservice.kafka.LeaveRejectedEvent;
import org.yuriy.leaveservice.kafka.LeaveRequestedEvent;
import org.yuriy.leaveservice.repository.LeaveRepository;
import org.yuriy.leaveservice.repository.specification.LeaveSpecification;
import org.yuriy.leaveservice.service.EmployeeClient;
import org.yuriy.leaveservice.service.LeaveService;

import java.util.ArrayList;
import java.util.List;

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
        leaveEventProducer.sendLeaveRequested(new LeaveRequestedEvent(
                leave.getId(),
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
        leaveEventProducer.sendLeaveApproved(new LeaveApprovedEvent(
                leave.getId(),
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
        leaveEventProducer.sendLeaveRejected(new LeaveRejectedEvent(
                leave.getId(),
                leave.getEmployeeId(),
                managerId
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

}
