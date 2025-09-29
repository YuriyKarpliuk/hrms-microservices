package org.yuriy.hrms.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.hrms.dto.mapper.EmployeeMapper;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.response.EmployeeResponse;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.exception.ResourceNotFoundException;
import org.yuriy.hrms.repository.EmployeeRepository;
import org.yuriy.hrms.service.EmployeeService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    @Override
    @Transactional
    public EmployeeResponse createNewEmployee(EmployeeCreateRequest req) {
        if (employeeRepository.existsByEmailAndOrgId(req.email(), req.orgId())) {
            throw new IllegalArgumentException("Email already exists in this org");
        }
        var e = employeeMapper.toEntity(req);
        validateEmployment(e);
        return employeeMapper.toResponse(employeeRepository.save(e));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeCreateRequest req) {
        var e = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        if (req.email() != null &&
                employeeRepository.existsByEmailAndOrgIdAndIdNot(req.email(), req.orgId(), id)) {
            throw new IllegalArgumentException("Email already exists in this org");
        }

        employeeMapper.applyPut(e, req);
        validateEmployment(e);
        return employeeMapper.toResponse(employeeRepository.save(e));
    }

    @Override
    @Transactional
    public EmployeeResponse patch(Long id, EmployeePatchRequest req) {
        var e = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        if (req.email() != null &&
                employeeRepository.existsByEmailAndOrgIdAndIdNot(req.email(), e.getOrgId(), id)) {
            throw new IllegalArgumentException("Email already exists in this org");
        }

        employeeMapper.applyPatch(e, req);
        validateEmployment(e);
        return employeeMapper.toResponse(employeeRepository.save(e));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id))
            throw new ResourceNotFoundException("Employee not found with id " + id);
        employeeRepository.deleteById(id);
    }

    private void validateEmployment(Employee e) {
        if (e.getStatus() == Status.TERMINATED && e.getTerminatedAt() == null) {
            throw new IllegalStateException("terminatedAt is required for TERMINATED status");
        }
        if (e.getHiredAt() != null && e.getTerminatedAt() != null &&
                e.getTerminatedAt().isBefore(e.getHiredAt())) {
            throw new IllegalStateException("terminatedAt must be >= hiredAt");
        }
    }
}
