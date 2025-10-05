package org.yuriy.department.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.department.dto.request.DepartmentCreateRequest;
import org.yuriy.department.dto.request.DepartmentPatchRequest;
import org.yuriy.department.dto.response.DepartmentResponse;
import org.yuriy.department.entity.Department;
import org.yuriy.department.repository.DepartmentRepository;

@Component
public class DepartmentMapper {

    private final DepartmentRepository departmentRepository;

    public DepartmentMapper(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department toEntity(DepartmentCreateRequest r) {
        Department parent = (departmentRepository.findById(r.parentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent department not found")));
        return Department.builder().name(r.name()).orgId(r.orgId()).managerId(r.managerId()).parent(parent)
                .build();
    }

    public void applyPatch(Department d, DepartmentPatchRequest r) {
        if (r.managerId() != null)
            d.setManagerId(r.managerId());
        if (r.name() != null)
            d.setName(r.name());
        if (r.parentId() != null) {
            Department parent = (departmentRepository.findById(r.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent department not found")));
            d.setParent(parent);
        }
    }


    public DepartmentResponse toResponse(Department d) {
        Long parentId = d.getParent() != null ? d.getParent().getId() : null;
        return new DepartmentResponse(d.getId(), d.getOrgId(), d.getName(), parentId, d.getManagerId());
    }
}
