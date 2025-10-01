package org.yuriy.department.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yuriy.department.dto.request.DepartmentCreateRequest;
import org.yuriy.department.dto.request.DepartmentPatchRequest;
import org.yuriy.department.dto.request.DepartmentSearchRequest;
import org.yuriy.department.dto.response.DepartmentResponse;

import java.util.List;


public interface DepartmentService {

    Page<DepartmentResponse> searchDepartments(DepartmentSearchRequest request, Pageable pageable);

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentById(Long id);

    DepartmentResponse createNewDepartment(DepartmentCreateRequest req);

    DepartmentResponse partialUpdateDepartment(Long id, DepartmentPatchRequest req);

    void deleteDepartment(Long id);
}
