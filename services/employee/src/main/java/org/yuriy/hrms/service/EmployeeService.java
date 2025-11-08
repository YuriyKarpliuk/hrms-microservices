package org.yuriy.hrms.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.request.EmployeeSearchRequest;
import org.yuriy.hrms.dto.response.EmployeeBasicResponse;
import org.yuriy.hrms.dto.response.EmployeeFullResponse;
import org.yuriy.hrms.dto.response.EmployeeResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface EmployeeService {

    Page<EmployeeResponse> searchEmployees(EmployeeSearchRequest request, Pageable pageable);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse createNewEmployee(EmployeeCreateRequest req);

    EmployeeResponse updateEmployee(Long id, EmployeeCreateRequest req);

    EmployeeResponse patch(Long id, EmployeePatchRequest req);

    void deleteEmployee(Long id);

    Boolean existsById(Long id);

    EmployeeBasicResponse getBasicInfo(Long id);

    EmployeeFullResponse getByEmail(String email);

    EmployeeResponse uploadAvatar(Long id, MultipartFile file);

    EmployeeFullResponse uploadCv(Long id, MultipartFile file) throws IOException;

    Resource downloadCv(Long id) throws FileNotFoundException, MalformedURLException;
}
