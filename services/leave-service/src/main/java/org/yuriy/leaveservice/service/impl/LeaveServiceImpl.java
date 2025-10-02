package org.yuriy.leaveservice.service.impl;

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
import org.yuriy.leaveservice.repository.LeaveRepository;
import org.yuriy.leaveservice.repository.specification.LeaveSpecification;
import org.yuriy.leaveservice.service.EmployeeClient;
import org.yuriy.leaveservice.service.LeaveService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveMapper leaveMapper;
    private final EmployeeClient employeeClient;

    public LeaveServiceImpl(LeaveRepository leaveRepository, LeaveMapper leaveMapper, EmployeeClient employeeClient) {
        this.leaveRepository = leaveRepository;
        this.leaveMapper = leaveMapper;
        this.employeeClient = employeeClient;
    }

    @Override
    @Transactional
    public LeaveResponse createLeave(LeaveCreateRequest req) {
        if (!employeeClient.existsById(req.employeeId())) {
            throw new IllegalArgumentException("Employee with id " + req.employeeId() + " not found");
        }
        if (req.endDate().isBefore(req.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        Leave leave = leaveMapper.toEntity(req);
        return leaveMapper.toResponse(leaveRepository.save(leave));
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
    public LeaveResponse approveLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(LeaveStatus.APPROVED);
        return leaveMapper.toResponse(leaveRepository.save(leave));
    }

    @Override
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(LeaveStatus.REJECTED);
        return leaveMapper.toResponse(leaveRepository.save(leave));
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
