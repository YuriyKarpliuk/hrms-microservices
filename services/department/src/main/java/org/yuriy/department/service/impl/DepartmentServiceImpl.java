package org.yuriy.department.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.department.dto.mapper.DepartmentMapper;
import org.yuriy.department.dto.request.DepartmentCreateRequest;
import org.yuriy.department.dto.request.DepartmentPatchRequest;
import org.yuriy.department.dto.request.DepartmentSearchRequest;
import org.yuriy.department.dto.response.DepartmentResponse;
import org.yuriy.department.entity.Department;
import org.yuriy.department.exception.ResourceNotFoundException;
import org.yuriy.department.kafka.DepartmentCreatedEvent;
import org.yuriy.department.kafka.DepartmentEventProducer;
import org.yuriy.department.kafka.DepartmentUpdatedEvent;
import org.yuriy.department.repository.DepartmentRepository;
import org.yuriy.department.repository.specification.DepartmentSpecification;
import org.yuriy.department.service.DepartmentService;
import org.yuriy.department.service.EmployeeClient;
import org.yuriy.department.service.OrganizationClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final OrganizationClient organizationClient;
    private final EmployeeClient employeeClient;
    private final DepartmentEventProducer departmentEventProducer;

    @Override
    public Page<DepartmentResponse> searchDepartments(DepartmentSearchRequest request, Pageable pageable) {
        List<Specification<Department>> specifications = new ArrayList<>();

        if (request.name() != null) {
            specifications.add(DepartmentSpecification.nameMatches(
                    request.name(), request.stringMatchType()
            ));
        }
        if (request.managerId() != null) {
            specifications.add(DepartmentSpecification.hasManager(request.managerId()));
        }

        if (request.orgId() != null) {
            specifications.add(DepartmentSpecification.inOrganization(request.orgId()));
        }

        Specification<Department> specification = Specification.allOf(specifications);

        return departmentRepository.findAll(specification, pageable)
                .map(departmentMapper::toResponse);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream().map(departmentMapper::toResponse).toList();
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        return departmentRepository.findById(id).map(departmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));
    }

    @Override
    @Transactional
    public DepartmentResponse createNewDepartment(DepartmentCreateRequest req) {
        if (!organizationClient.existsById(req.orgId())) {
            throw new IllegalArgumentException("Organization with id " + req.orgId() + " not found");
        }
        if (!employeeClient.existsById(req.managerId())) {
            throw new IllegalArgumentException("Employee (manager) with id " + req.managerId() + " not found");
        }
        if (!employeeClient.getEmployeeBasicInfo(req.managerId()).roles().contains("MANAGER")) {
            throw new IllegalArgumentException("Employee with id " + req.managerId() + " is not a manager");
        }
        if (departmentRepository.existsByNameAndOrgId(req.name(), req.orgId())) {
            throw new IllegalArgumentException("Department with such name already exists in this org");
        }
        var d = departmentMapper.toEntity(req);
        departmentRepository.save(d);
        departmentEventProducer.sendDepartmentCreated(
                new DepartmentCreatedEvent(d.getId(), d.getOrgId(), d.getName(), d.getParent().getId(),
                        d.getManagerId()));
        return departmentMapper.toResponse(d);
    }

    @Override
    @Transactional
    public DepartmentResponse partialUpdateDepartment(Long id, DepartmentPatchRequest req) {
        var d = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id " + id));

        if (req.name() != null &&
                departmentRepository.existsByNameAndOrgId(req.name(), d.getOrgId())) {
            throw new IllegalArgumentException("Department with such name already exists in this org");
        }

        departmentMapper.applyPatch(d, req);
        departmentRepository.save(d);
        departmentEventProducer.sendDepartmentUpdated(
                new DepartmentUpdatedEvent(d.getId(), d.getOrgId(), d.getName(), d.getParent().getId(),
                        d.getManagerId()));
        return departmentMapper.toResponse(d);
    }


    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Department not found with id " + id));

        departmentRepository.deleteById(id);
        departmentEventProducer.sendDepartmentUpdated(
                new DepartmentUpdatedEvent(department.getId(), department.getOrgId(), department.getName(),
                        department.getParent().getId(),
                        department.getManagerId()));
    }

}
