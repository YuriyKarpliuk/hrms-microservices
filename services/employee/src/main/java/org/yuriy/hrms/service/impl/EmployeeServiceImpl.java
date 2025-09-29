package org.yuriy.hrms.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.hrms.dto.mapper.EmployeeMapper;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.repository.EmployeeRepository;
import org.yuriy.hrms.service.EmployeeService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository repo, EmployeeMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<Employee> getEmployeesByOrgAndStatus(Long orgId, Employee.Status status) {
        var effective = (status == null) ? Employee.Status.ACTIVE : status;
        return repo.findByOrgIdAndStatus(orgId, effective);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    @Override
    @Transactional
    public Employee createNewEmployee(EmployeeCreateRequest req) {
        if (repo.existsByEmailAndOrgId(req.email(), req.orgId())) {
            throw new IllegalArgumentException("Email already exists in this org");
        }
        var e = mapper.toEntity(req);
        validateEmployment(e);
        return repo.save(e);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeCreateRequest req) {
        var e = getEmployeeById(id);

        var newEmail = req.email();
        var newOrgId = req.orgId();
        if (newEmail != null && newOrgId != null &&
                repo.existsByEmailAndOrgIdAndIdNot(newEmail, newOrgId, id)) {
            throw new IllegalArgumentException("Email already exists in this org");
        }

        mapper.applyPut(e, req);
        validateEmployment(e);
        return e;
    }

    @Override
    @Transactional
    public Employee patch(Long id, EmployeePatchRequest req) {
        var e = getEmployeeById(id);

        if (req.email() != null &&
                repo.existsByEmailAndOrgIdAndIdNot(req.email(), e.getOrgId(), id)) {
            throw new IllegalArgumentException("Email already exists in this org");
        }

        mapper.applyPatch(e, req);
        validateEmployment(e);
        return e;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!repo.existsById(id))
            throw new EntityNotFoundException("Employee not found");
        repo.deleteById(id);
    }

    private void validateEmployment(Employee e) {
        if (e.getStatus() == Employee.Status.TERMINATED && e.getTerminatedAt() == null) {
            throw new IllegalStateException("terminatedAt is required for TERMINATED status");
        }
        if (e.getHiredAt() != null && e.getTerminatedAt() != null &&
                e.getTerminatedAt().isBefore(e.getHiredAt())) {
            throw new IllegalStateException("terminatedAt must be >= hiredAt");
        }
    }
}
