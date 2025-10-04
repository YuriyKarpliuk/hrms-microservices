package org.yuriy.hrms.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.response.EmployeeResponse;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.service.KeycloakUserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final KeycloakUserService keycloakUserService;

    public Employee toEntity(EmployeeCreateRequest r) {
        return Employee.builder().orgId(r.orgId()).deptId(r.deptId()).position(r.position())
                .managerId(r.managerId()).hrId(r.hrId()).email(r.email()).firstName(r.firstName())
                .lastName(r.lastName()).phone(r.phone()).status(r.status()).gender(r.gender())
                .maritalStatus(r.maritalStatus()).taxNumber(r.taxNumber()).about(r.about())
                .officeLocation(r.officeLocation()).birthDate(r.birthDate()).hiredAt(r.hiredAt()).build();
    }

    public void applyPatch(Employee e, EmployeePatchRequest r) {
        if (r.email() != null)
            e.setEmail(r.email());
        if (r.firstName() != null)
            e.setFirstName(r.firstName());
        if (r.lastName() != null)
            e.setLastName(r.lastName());
        if (r.phone() != null)
            e.setPhone(r.phone());
        if (r.status() != null)
            e.setStatus(r.status());
        if (r.deptId() != null)
            e.setDeptId(r.deptId());
        if (r.position() != null)
            e.setPosition(r.position());
        if (r.managerId() != null)
            e.setManagerId(r.managerId());
        if (r.hrId() != null)
            e.setHrId(r.hrId());
        if (r.gender() != null)
            e.setGender(r.gender());
        if (r.maritalStatus() != null)
            e.setMaritalStatus(r.maritalStatus());
        if (r.taxNumber() != null)
            e.setTaxNumber(r.taxNumber());
        if (r.about() != null)
            e.setAbout(r.about());
        if (r.officeLocation() != null)
            e.setOfficeLocation(r.officeLocation());
        if (r.birthDate() != null)
            e.setBirthDate(r.birthDate());
        if (r.hiredAt() != null)
            e.setHiredAt(r.hiredAt());
        if (r.terminatedAt() != null)
            e.setTerminatedAt(r.terminatedAt());
        if (r.avatarUrl() != null)
            e.setAvatarUrl(r.avatarUrl());
    }

    public void applyPut(Employee e, EmployeeCreateRequest r) {
        e.setOrgId(r.orgId());
        e.setDeptId(r.deptId());
        e.setPosition(r.position());
        e.setManagerId(r.managerId());
        e.setHrId(r.hrId());
        e.setEmail(r.email());
        e.setFirstName(r.firstName());
        e.setLastName(r.lastName());
        e.setPhone(r.phone());
        e.setStatus(r.status());
        e.setGender(r.gender());
        e.setMaritalStatus(r.maritalStatus());
        e.setTaxNumber(r.taxNumber());
        e.setAbout(r.about());
        e.setOfficeLocation(r.officeLocation());
        e.setBirthDate(r.birthDate());
        e.setHiredAt(r.hiredAt());
    }

    public EmployeeResponse toResponse(Employee e) {
        List<String> roles = keycloakUserService.getUserRoles(e.getUserId());
        return new EmployeeResponse(e.getId(), e.getOrgId(), e.getUserId(), e.getDeptId(), e.getPosition(),
                e.getManagerId(), e.getHrId(), e.getEmail(), roles, e.getFirstName(), e.getLastName(), e.getPhone(),
                e.getStatus(), e.getGender(), e.getMaritalStatus(), e.getTaxNumber(), e.getAbout(),
                e.getOfficeLocation(), e.getBirthDate(), e.getAge(), e.getHiredAt(), e.getTerminatedAt(),
                e.getAvatarUrl(), e.getCvKey());
    }
}
