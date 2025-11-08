package org.yuriy.hrms.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yuriy.hrms.configuration.AppProperties;
import org.yuriy.hrms.dto.mapper.EmployeeMapper;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.request.EmployeeSearchRequest;
import org.yuriy.hrms.dto.response.EmployeeBasicResponse;
import org.yuriy.hrms.dto.response.EmployeeFullResponse;
import org.yuriy.hrms.dto.response.EmployeeResponse;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.exception.ResourceNotFoundException;
import org.yuriy.hrms.kafka.EmployeeCreatedEvent;
import org.yuriy.hrms.kafka.EmployeeDeletedEvent;
import org.yuriy.hrms.kafka.EmployeeEventProducer;
import org.yuriy.hrms.kafka.EmployeeUpdatedEvent;
import org.yuriy.hrms.repository.EmployeeRepository;
import org.yuriy.hrms.repository.specification.EmployeeSpecification;
import org.yuriy.hrms.service.DepartmentClient;
import org.yuriy.hrms.service.EmployeeService;
import org.yuriy.hrms.service.KeycloakUserService;
import org.yuriy.hrms.service.OrganizationClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final KeycloakUserService keycloakUserService;
    private final EmployeeEventProducer employeeEventProducer;
    private final AppProperties appProperties;
    private final OrganizationClient organizationClient;
    private final DepartmentClient departmentClient;


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

        employeeRepository.save(e);
        employeeEventProducer.sendEmployeeCreated(new EmployeeCreatedEvent(
                e.getId(),
                e.getOrgId(),
                e.getDeptId(),
                e.getEmail(),
                e.getFirstName(),
                e.getLastName(),
                e.getPosition(),
                e.getHiredAt(),
                req.status().toString()
        ));
        return employeeMapper.toResponse(e);
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

        employeeRepository.save(e);

        employeeEventProducer.sendEmployeeUpdated(new EmployeeUpdatedEvent(
                e.getId(),
                e.getEmail(),
                e.getPosition(),
                e.getDeptId(),
                e.getOrgId()
        ));
        return employeeMapper.toResponse(e);
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
        employeeRepository.save(e);

        employeeEventProducer.sendEmployeeUpdated(new EmployeeUpdatedEvent(
                e.getId(),
                e.getEmail(),
                e.getPosition(),
                e.getDeptId(),
                e.getOrgId()
        ));
        return employeeMapper.toResponse(e);
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
        employeeEventProducer.sendEmployeeDeleted(new EmployeeDeletedEvent(
                employee.getId(),
                employee.getOrgId(),
                employee.getDeptId(),
                employee.getTerminatedAt() != null ? employee.getTerminatedAt() : LocalDate.now(), Status.TERMINATED.toString()));
    }

    @Override
    public Boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }

    @Override
    public EmployeeBasicResponse getBasicInfo(Long id) {
        return employeeRepository.findById(id)
                .map(emp -> {
                    List<String> roles = keycloakUserService.getUserRoles(emp.getUserId());
                    return new EmployeeBasicResponse(emp.getId(), emp.getFirstName(), emp.getLastName(),
                            emp.getEmail(), emp.getPosition(), roles);
                })
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    @Override
    public EmployeeFullResponse getByEmail(String email) {
        Employee employee = employeeRepository.findEmployeeByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for email: " + email));

        String deptName = departmentClient.getNameById(employee.getDeptId());
        String orgName = organizationClient.getNameById(employee.getOrgId());

        Employee manager = employeeRepository.findById(employee.getManagerId()).orElse(null);
        Employee hr = employeeRepository.findById(employee.getHrId()).orElse(null);

        return employeeMapper.toResponse(employee, deptName, orgName, manager, hr);
    }

    @Override
    @Transactional
    public EmployeeResponse uploadAvatar(Long id, MultipartFile file) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        try {
            Path uploadDir = Paths.get(appProperties.getUpload().getAvatarDir());
            Files.createDirectories(uploadDir);

            String extension = getFileExtension(file.getOriginalFilename());
            String hashName = DigestUtils.sha256Hex(UUID.randomUUID() + "_" + file.getOriginalFilename());
            String fileName = hashName + (extension != null ? "." + extension : "");

            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            if (employee.getAvatarUrl() != null && employee.getAvatarUrl().contains("/uploads/avatars/")) {
                String oldFileName = employee.getAvatarUrl().substring(employee.getAvatarUrl().lastIndexOf("/") + 1);
                Path oldFilePath = uploadDir.resolve(oldFileName);
                Files.deleteIfExists(oldFilePath);
            }

            String avatarUrl = appProperties.getBaseUrl() + "/uploads/avatars/" + fileName;

            employee.setAvatarUrl(avatarUrl);
            employeeRepository.save(employee);

            return employeeMapper.toResponse(employee);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public EmployeeFullResponse uploadCv(Long id, MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".pdf"))
            throw new IllegalArgumentException("Only PDF files are allowed.");

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Path uploadDir = Paths.get(appProperties.getUpload().getCvDir());
        Files.createDirectories(uploadDir);

        String fileName = DigestUtils.sha256Hex(UUID.randomUUID() + "_" + file.getOriginalFilename()) + ".pdf";
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());

        String cvUrl = appProperties.getBaseUrl() + "/uploads/cv/" + fileName;
        employee.setCvKey(cvUrl);

        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadCv(Long id) throws FileNotFoundException, MalformedURLException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        String cvKey = employee.getCvKey();
        if (cvKey == null || cvKey.isBlank()) {
            throw new FileNotFoundException("CV not uploaded");
        }

        String fileName = Paths.get(URI.create(cvKey).getPath()).getFileName().toString();

        Path filePath = Paths.get(appProperties.getUpload().getCvDir()).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("CV file not found at path: " + filePath);
        }

        return resource;
    }



    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return null;
        return fileName.substring(fileName.lastIndexOf('.') + 1);
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
