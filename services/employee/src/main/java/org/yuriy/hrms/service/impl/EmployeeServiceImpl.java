package org.yuriy.hrms.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.hrms.dto.mapper.EmployeeMapper;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.request.EmployeeSearchRequest;
import org.yuriy.hrms.dto.response.EmployeeBasicResponse;
import org.yuriy.hrms.dto.response.EmployeeResponse;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.exception.ResourceNotFoundException;
import org.yuriy.hrms.repository.EmployeeRepository;
import org.yuriy.hrms.repository.specification.EmployeeSpecification;
import org.yuriy.hrms.service.EmployeeService;
import org.yuriy.hrms.service.KeycloakUserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final KeycloakUserService keycloakUserService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
            KeycloakUserService keycloakUserService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.keycloakUserService = keycloakUserService;
    }

    @Override
    public Page<EmployeeResponse> searchEmployees(EmployeeSearchRequest request, Pageable pageable) {
        List<Specification<Employee>> specifications = new ArrayList<>();

        if (request.firstName() != null) {
            specifications.add(EmployeeSpecification.firstNameMatches(
                    request.firstName(), request.firstNameMatchType()
            ));
        }
        if (request.lastName() != null) {
            specifications.add(EmployeeSpecification.lastNameMatches(
                    request.lastName(), request.lastNameMatchType()
            ));
        }
        if (request.email() != null) {
            specifications.add(EmployeeSpecification.emailMatches(
                    request.email(), request.emailMatchType()
            ));
        }
        if (request.status() != null) {
            specifications.add(EmployeeSpecification.hasStatus(request.status()));
        }
        if (request.gender() != null) {
            specifications.add(EmployeeSpecification.hasGender(request.gender()));
        }
        if (request.maritalStatus() != null) {
            specifications.add(EmployeeSpecification.hasMaritalStatus(request.maritalStatus()));
        }
        if (request.hiredFrom() != null || request.hiredTo() != null) {
            specifications.add(EmployeeSpecification.hiredBetween(request.hiredFrom(), request.hiredTo()));
        }
        if (request.birthFrom() != null || request.birthTo() != null) {
            specifications.add(EmployeeSpecification.hasBirthdayBetween(request.birthFrom(), request.birthTo()));
        }
        if (request.officeLocation() != null) {
            specifications.add(EmployeeSpecification.hasOfficeLocation(request.officeLocation()));
        }
        if (request.deptId() != null) {
            specifications.add(EmployeeSpecification.inDepartment(request.deptId()));
        }

        if (request.managerId() != null) {
            specifications.add(EmployeeSpecification.hasManager(request.managerId()));
        }

        if (request.hrId() != null) {
            specifications.add(EmployeeSpecification.hasHR(request.hrId()));
        }

        if (request.phone() != null) {
            specifications.add(EmployeeSpecification.hasPhone(request.phone()));
        }
        Specification<Employee> specification = Specification.allOf(specifications);

        return employeeRepository.findAll(specification, pageable)
                .map(employeeMapper::toResponse);
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
        String role = StringUtils.isBlank(req.role()) ? "USER" : req.role();
        String keyCloakUserId =
                keycloakUserService.createUser(req.email(), req.firstName(), req.lastName(), role);
        var e = employeeMapper.toEntity(req);
        e.setUserId(keyCloakUserId);
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
        if (e.getUserId() != null) {
            keycloakUserService.updateUser(e.getUserId(), e.getEmail(), e.getEmail(), e.getFirstName(),
                    e.getLastName());
        }
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
        if (e.getUserId() != null) {
            keycloakUserService.updateUser(e.getUserId(), e.getEmail(), e.getEmail(), e.getFirstName(),
                    e.getLastName());
        }
        return employeeMapper.toResponse(employeeRepository.save(e));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Employee not found with id " + id));
        String userId = employee.getUserId();
        if (userId != null) {
            keycloakUserService.deleteUser(userId);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public Boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }

    @Override
    public EmployeeBasicResponse getBasicInfo(Long id) {
        return employeeRepository.findById(id)
                .map(emp -> new EmployeeBasicResponse(emp.getId(), emp.getFirstName(), emp.getLastName(),
                        emp.getEmail(), emp.getPosition()))
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
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
