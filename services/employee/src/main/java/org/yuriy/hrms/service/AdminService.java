package org.yuriy.hrms.service;


import org.yuriy.hrms.dto.request.*;
import org.yuriy.hrms.dto.response.AdminDashboardResponse;
import org.yuriy.hrms.dto.response.PageResponse;

import java.util.List;

public interface AdminService {
    List<OrganizationClient.OrganizationResponse> getOrganizations();

    OrganizationClient.OrganizationResponse createOrganization(OrganizationCreateRequest req);

    OrganizationClient.OrganizationResponse updateOrganization(Long id, OrganizationPatchRequest req);

    void deleteOrganization(Long id);

    PageResponse<OrganizationClient.OrganizationResponse> searchOrganizations(OrganizationSearchRequest req, int page, int size);

    List<DepartmentClient.DepartmentResponse> getDepartments();

    List<DepartmentClient.DepartmentResponse> getDepartmentsByOrg(Long orgId);

    PageResponse<DepartmentClient.DepartmentResponse> searchDepartments(
            DepartmentSearchRequest req, int page, int size
    );

    DepartmentClient.DepartmentResponse createDepartment(DepartmentCreateRequest req);

    DepartmentClient.DepartmentResponse updateDepartment(Long id, DepartmentPatchRequest req);

    void deleteDepartment(Long id);

    AdminDashboardResponse getDashboard();

}
