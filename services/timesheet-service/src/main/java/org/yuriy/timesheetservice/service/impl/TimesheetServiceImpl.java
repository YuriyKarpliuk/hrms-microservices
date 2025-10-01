package org.yuriy.timesheetservice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.timesheetservice.dto.mapper.TimesheetMapper;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.entity.Timesheet;
import org.yuriy.timesheetservice.entity.TimesheetStatus;
import org.yuriy.timesheetservice.repository.TimesheetRepository;
import org.yuriy.timesheetservice.repository.specification.TimesheetSpecification;
import org.yuriy.timesheetservice.service.TimesheetService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional()
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final TimesheetMapper timesheetMapper;

    public TimesheetServiceImpl(TimesheetRepository timesheetRepository, TimesheetMapper timesheetMapper) {
        this.timesheetRepository = timesheetRepository;
        this.timesheetMapper = timesheetMapper;
    }

    @Override
    public TimesheetResponse createTimesheet(TimesheetCreateRequest req) {
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
        return timesheetMapper.toResponse(timesheetRepository.save(ts));
    }

    @Transactional
    @Override
    public TimesheetResponse rejectTimesheet(Long id) {
        Timesheet ts = timesheetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet not found with id " + id));
        ts.setStatus(TimesheetStatus.REJECTED);
        return timesheetMapper.toResponse(timesheetRepository.save(ts));
    }

}
