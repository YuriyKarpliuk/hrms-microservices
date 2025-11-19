package org.yuriy.hrms.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.yuriy.hrms.dto.response.*;
import org.yuriy.hrms.entity.Employee;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.exception.ResourceNotFoundException;
import org.yuriy.hrms.kafka.EmployeeCreatedEvent;
import org.yuriy.hrms.kafka.EmployeeDeletedEvent;
import org.yuriy.hrms.kafka.EmployeeEventProducer;
import org.yuriy.hrms.kafka.EmployeeUpdatedEvent;
import org.yuriy.hrms.repository.EmployeeRepository;
import org.yuriy.hrms.repository.specification.EmployeeSpecification;
import org.yuriy.hrms.service.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final KeycloakUserService keycloakUserService;
    private final EmployeeEventProducer employeeEventProducer;
    private final AppProperties appProperties;
    private final OrganizationClient organizationClient;
    private final DepartmentClient departmentClient;
    private final PayrollClient payrollClient;
    private final TimesheetClient timesheetClient;
    private final LeaveClient leaveClient;


    @Override
    public Page<EmployeeResponse> searchEmployees(Long employeeId, EmployeeSearchRequest request, Pageable pageable) {
        var e = employeeRepository.findById(employeeId);
        Long currentUserOrgId = e.get().getOrgId();
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

        specifications.add(EmployeeSpecification.excludeEmployeeId(employeeId));

        specifications.add(EmployeeSpecification.hasOrgId(currentUserOrgId));

        Specification<Employee> specification = Specification.allOf(specifications);

        return employeeRepository.findAll(specification, pageable)
                .map(employee -> {
                    String orgName = null;
                    String deptName = null;
                    AtomicReference<String> managerName = new AtomicReference<>();
                    AtomicReference<String> hrName = new AtomicReference<>();

                    try {
                        if (employee.getOrgId() != null)
                            orgName = organizationClient.getNameById(employee.getOrgId());
                    } catch (Exception ex) {
                        log.warn("Failed to fetch org name for {}: {}", employee.getOrgId(), ex.getMessage());
                    }

                    try {
                        if (employee.getDeptId() != null)
                            deptName = departmentClient.getNameById(employee.getDeptId());
                    } catch (Exception ex) {
                        log.warn("Failed to fetch dept name for {}: {}", employee.getDeptId(), ex.getMessage());
                    }

                    if (employee.getManagerId() != null) {
                        employeeRepository.findById(employee.getManagerId())
                                .ifPresent(m -> managerName.set(m.getFirstName() + " " + m.getLastName()));
                    }
                    if (employee.getHrId() != null) {
                        employeeRepository.findById(employee.getHrId())
                                .ifPresent(h -> hrName.set(h.getFirstName() + " " + h.getLastName()));
                    }

                    return employeeMapper.toDetailedResponse(
                            employee,
                            orgName,
                            deptName,
                            managerName.get(),
                            hrName.get()
                    );
                });
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        String organizationName = null;
        String departmentName = null;
        AtomicReference<String> managerFullName = new AtomicReference<>();
        AtomicReference<String> hrFullName = new AtomicReference<>();

        try {
            if (employee.getOrgId() != null) {
                organizationName = organizationClient.getNameById(employee.getOrgId());
            }
        } catch (Exception ex) {
            log.warn("Failed to fetch organization name for {}: {}", employee.getOrgId(), ex.getMessage());
        }

        try {
            if (employee.getDeptId() != null) {
                departmentName = departmentClient.getNameById(employee.getDeptId());
            }
        } catch (Exception ex) {
            log.warn("Failed to fetch department name for {}: {}", employee.getDeptId(), ex.getMessage());
        }

        if (employee.getManagerId() != null) {
            employeeRepository.findById(employee.getManagerId())
                    .ifPresent(m -> managerFullName.set(m.getFirstName() + " " + m.getLastName()));
        }

        if (employee.getHrId() != null) {
            employeeRepository.findById(employee.getHrId())
                    .ifPresent(h -> hrFullName.set(h.getFirstName() + " " + h.getLastName()));
        }

        return employeeMapper.toDetailedResponse(employee, organizationName, departmentName, managerFullName.get(),
                hrFullName.get());
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
                employee.getTerminatedAt() != null ? employee.getTerminatedAt() : LocalDate.now(),
                Status.TERMINATED.toString()));
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
                            emp.getEmail(), emp.getPosition(), emp.getBirthDate(), roles);
                })
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    @Override
    public EmployeeFullResponse getByEmail(String email) {
        Employee employee = employeeRepository.findEmployeeByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for email: " + email));
        String deptName = null;
        String orgName = null;
        if (employee.getDeptId() != null) {
             deptName = departmentClient.getNameById(employee.getDeptId());
        }
        if (employee.getOrgId() != null) {
             orgName = organizationClient.getNameById(employee.getOrgId());
        }
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

    @Override
    public Optional<Object> findEmployeeIdByEmail(String email) {
        return employeeRepository.findByEmail(email).map(Employee::getId);
    }

    @Override
    public UserDashboardResponse getDashboard(Long employeeId) {

        EmployeeResponse employeeResponse = getEmployeeById(employeeId);
        EmployeeFullResponse employeeFullResponse = getByEmail(employeeResponse.email());

        String name = employeeFullResponse.firstName() + " " + employeeFullResponse.lastName();
        String department = employeeFullResponse.department() != null
                ? employeeFullResponse.department().name()
                : "N/A";

        Map<String, Object> timesheetStats = Map.of();
        try {
            timesheetStats = timesheetClient.getWeeklySummary(employeeId);
        } catch (Exception e) {
            log.warn("Failed to fetch timesheet summary for {}: {}", employeeId, e.getMessage());
        }

        double hoursThisWeek = ((Number) timesheetStats.getOrDefault("hoursWorked", 0)).doubleValue();
        double hoursTarget = 40.0;

        double vacationDaysLeft = 0.0;
        double sickLeaveLeft = 0.0;
        double unpaidLeaveLeft = 0.0;
        double vacationDaysUsed = 0.0;

        try {
            List<Map<String, Object>> summary = leaveClient.getLeaveSummary(employeeId);
            for (Map<String, Object> s : summary) {
                String type = (String) s.get("type");
                long total = ((Number) s.getOrDefault("total", 0)).longValue();
                long used = ((Number) s.getOrDefault("used", 0)).longValue();
                long remaining = total - used;

                switch (type) {
                    case "VACATION" -> {
                        vacationDaysLeft = remaining;
                        vacationDaysUsed = used;
                    }
                    case "SICK" -> sickLeaveLeft = remaining;
                    case "UNPAID" -> unpaidLeaveLeft = remaining;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch leave summary for {}: {}", employeeId, e.getMessage());
        }

        List<Map<String, Object>> upcomingLeaveData = List.of();
        try {
            upcomingLeaveData = leaveClient.getUpcoming(employeeId);
        } catch (Exception e) {
            log.warn("Failed to fetch upcoming leaves for {}: {}", employeeId, e.getMessage());
        }

        List<LeaveUpcomingResponse> upcomingLeaves = upcomingLeaveData.stream()
                .map(l -> new LeaveUpcomingResponse(
                        LocalDate.parse((String) l.get("startDate")),
                        LocalDate.parse((String) l.get("endDate")),
                        (String) l.getOrDefault("type", "LEAVE")
                ))
                .toList();

        List<Map<String, Object>> payrolls = List.of();
        try {
            payrolls = payrollClient.getPayrollsByEmployee(employeeId);
        } catch (Exception e) {
            log.warn("Failed to fetch payrolls for {}: {}", employeeId, e.getMessage());
        }

        List<PayrollSummary> recentPayrolls = payrolls.stream()
                .filter(p -> p.get("periodEnd") != null)
                .sorted((a, b) -> ((String) b.get("periodEnd")).compareTo((String) a.get("periodEnd")))
                .limit(3)
                .map(p -> new PayrollSummary(
                        LocalDate.parse((String) p.get("periodEnd")),
                        new BigDecimal(p.get("netSalary").toString()),
                        (String) p.getOrDefault("status", "UNKNOWN")
                ))
                .toList();

        BigDecimal currentSalary = recentPayrolls.isEmpty()
                ? BigDecimal.ZERO
                : recentPayrolls.get(0).netSalary();

        return new UserDashboardResponse(
                name,
                department,
                currentSalary,
                hoursThisWeek,
                hoursTarget,
                vacationDaysLeft,
                sickLeaveLeft,
                unpaidLeaveLeft,
                vacationDaysUsed,
                recentPayrolls,
                upcomingLeaves
        );
    }

    @Override
    public List<EmployeeBasicResponse> getEmployeesByManager(Long managerId) {
        return employeeRepository.findByManagerId(managerId)
                .stream()
                .map(emp -> {
                    List<String> roles = keycloakUserService.getUserRoles(emp.getUserId());
                    return new EmployeeBasicResponse(
                            emp.getId(),
                            emp.getFirstName(),
                            emp.getLastName(),
                            emp.getEmail(),
                            emp.getPosition(),
                            emp.getBirthDate(),
                            roles
                    );
                })
                .toList();
    }

    @Override
    public ManagerDashboardResponse buildManagerDashboard(Long managerId) {
        var employees = getEmployeesByManager(managerId);
        int totalEmployees = employees.size();

        var leavePage = leaveClient.getTeamLeaves(
                managerId,
                null, null, null, null, null, null,
                Pageable.unpaged()
        );
        var leaves = Optional.of(leavePage.getContent()).orElse(List.of());
        int onLeave = (int) leaves.stream()
                .filter(l -> "APPROVED".equalsIgnoreCase(l.status()))
                .count();

        var timesheetPage = timesheetClient.getTeamTimesheets(
                managerId,
                null, null, null, null, null,
                Pageable.unpaged()
        );
        var timesheets = Optional.of(timesheetPage.getContent()).orElse(List.of());
        int pendingTimesheets = (int) timesheets.stream()
                .filter(t -> "SUBMITTED".equalsIgnoreCase(t.status()))
                .count();

        var payrollPage = payrollClient.getTeamPayrolls(
                managerId,
                null, null, null, null, null,
                Pageable.unpaged()
        );
        var payrolls = Optional.of(payrollPage.getContent()).orElse(List.of());
        int approvedPayrolls = (int) payrolls.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.status()))
                .count();

        List<ManagerTopPerformer> topPerformers = timesheets.stream()
                .collect(Collectors.groupingBy(
                        TimesheetResponse::employeeId,
                        Collectors.summingDouble(t -> Optional.ofNullable(t.totalHours()).orElse(0.0))
                ))
                .entrySet().stream()
                .map(e -> {
                    var emp = employees.stream()
                            .filter(x -> Objects.equals(x.id(), e.getKey()))
                            .findFirst()
                            .orElse(null);
                    if (emp == null) return null;

                    boolean isOnLeave = leaves.stream()
                            .anyMatch(l -> Objects.equals(l.employeeId(), e.getKey())
                                    && "APPROVED".equalsIgnoreCase(l.status()));

                    return new ManagerTopPerformer(
                            e.getKey(),
                            emp.firstName() + " " + emp.lastName(),
                            e.getValue(),
                            isOnLeave
                    );
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(ManagerTopPerformer::hours).reversed())
                .limit(10)
                .toList();

        List<ManagerActivityItem> feed = new ArrayList<>();

        List<ManagerActivityItem> finalFeed = feed;
        timesheets.stream()
                .filter(t -> t.updatedAt() != null)
                .sorted(Comparator.comparing(TimesheetResponse::updatedAt).reversed())
                .limit(2)
                .forEach(t -> finalFeed.add(new ManagerActivityItem(
                        "TIMESHEET",
                        "Timesheet " + t.status() + " for " + t.employeeFirstName(),
                        t.updatedAt()
                )));

        List<ManagerActivityItem> finalFeed1 = feed;
        leaves.stream()
                .filter(l -> l.startDate() != null)
                .sorted(Comparator.comparing(LeaveResponse::startDate).reversed())
                .limit(2)
                .forEach(l -> finalFeed1.add(new ManagerActivityItem(
                        "LEAVE",
                        "Leave " + l.status() + " for " + l.employeeFirstName(),
                        l.startDate().atStartOfDay()
                )));

        List<ManagerActivityItem> finalFeed2 = feed;
        payrolls.stream()
                .filter(p -> p.periodEnd() != null)
                .sorted(Comparator.comparing(PayrollResponse::periodEnd).reversed())
                .limit(1)
                .forEach(p -> finalFeed2.add(new ManagerActivityItem(
                        "PAYROLL",
                        "Payroll " + p.status() + " for " + p.employeeFirstName(),
                        p.periodEnd().atStartOfDay()
                )));

        feed.sort(Comparator.comparing(ManagerActivityItem::timestamp).reversed());
        feed = feed.stream().limit(5).toList();

        return new ManagerDashboardResponse(
                totalEmployees,
                onLeave,
                pendingTimesheets,
                approvedPayrolls,
                topPerformers,
                feed
        );
    }

    @Override
    public List<EmployeeResponse> getEmployeesByOrganization(Long orgId) {
        return employeeRepository.findByOrgId(orgId)
                .stream()
                .map(employee -> {
                    String orgName = null;
                    String deptName = null;
                    AtomicReference<String> managerName = new AtomicReference<>();
                    AtomicReference<String> hrName = new AtomicReference<>();

                    try {
                        if (employee.getOrgId() != null)
                            orgName = organizationClient.getNameById(employee.getOrgId());
                    } catch (Exception ex) {
                        log.warn("Failed to fetch org name for {}: {}", employee.getOrgId(), ex.getMessage());
                    }

                    try {
                        if (employee.getDeptId() != null)
                            deptName = departmentClient.getNameById(employee.getDeptId());
                    } catch (Exception ex) {
                        log.warn("Failed to fetch dept name for {}: {}", employee.getDeptId(), ex.getMessage());
                    }

                    if (employee.getManagerId() != null) {
                        employeeRepository.findById(employee.getManagerId())
                                .ifPresent(m -> managerName.set(m.getFirstName() + " " + m.getLastName()));
                    }
                    if (employee.getHrId() != null) {
                        employeeRepository.findById(employee.getHrId())
                                .ifPresent(h -> hrName.set(h.getFirstName() + " " + h.getLastName()));
                    }

                    return employeeMapper.toDetailedResponse(employee, orgName, deptName, managerName.get(), hrName.get());
                })
                .toList();
    }


    @Override
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDeptId(departmentId)
                .stream()
                .map(employee -> {
                    String orgName = null;
                    String deptName = null;
                    AtomicReference<String> managerName = new AtomicReference<>();
                    AtomicReference<String> hrName = new AtomicReference<>();

                    try {
                        if (employee.getOrgId() != null)
                            orgName = organizationClient.getNameById(employee.getOrgId());
                    } catch (Exception ex) {
                        log.warn("Failed to fetch org name for {}: {}", employee.getOrgId(), ex.getMessage());
                    }

                    try {
                        if (employee.getDeptId() != null)
                            deptName = departmentClient.getNameById(employee.getDeptId());
                    } catch (Exception ex) {
                        log.warn("Failed to fetch dept name for {}: {}", employee.getDeptId(), ex.getMessage());
                    }

                    if (employee.getManagerId() != null) {
                        employeeRepository.findById(employee.getManagerId())
                                .ifPresent(m -> managerName.set(m.getFirstName() + " " + m.getLastName()));
                    }
                    if (employee.getHrId() != null) {
                        employeeRepository.findById(employee.getHrId())
                                .ifPresent(h -> hrName.set(h.getFirstName() + " " + h.getLastName()));
                    }

                    return employeeMapper.toDetailedResponse(employee, orgName, deptName, managerName.get(), hrName.get());
                })
                .toList();
    }

    @Override
    public List<EmployeeResponse> getTodayBirthdaysForOrg(Long orgId) {
        LocalDate today = LocalDate.now();

        return employeeRepository.findByOrgId(orgId).stream()
                .filter(e -> e.getBirthDate() != null)
                .filter(e ->
                        e.getBirthDate().getMonthValue() == today.getMonthValue() &&
                                e.getBirthDate().getDayOfMonth() == today.getDayOfMonth()
                )
                .map(employeeMapper::toResponse)
                .toList();
    }

    public List<EmployeeBasicResponse> getManagers() {
        return employeeRepository.findAll()
                .stream()
                .filter(e -> keycloakUserService.getUserRoles(e.getUserId()).contains("MANAGER"))
                .map(emp -> {
                    List<String> roles = keycloakUserService.getUserRoles(emp.getUserId());
                    return new EmployeeBasicResponse(emp.getId(), emp.getFirstName(), emp.getLastName(),
                            emp.getEmail(), emp.getPosition(), emp.getBirthDate(), roles);
                }).toList();
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains("."))
            return null;
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

    @Override
    public long countEmployees() {
        return employeeRepository.count();
    }


    @Override
    public List<Integer> getRoleDistribution() {
        int admins = countUsersByRole("ADMIN");
        int hr = countUsersByRole("HR");
        int managers = countUsersByRole("MANAGER");
        int users = countUsersByRole("USER");

        return List.of(admins, hr, managers, users);
    }

    @Override
    public int countCreatedOn(String day) {
        LocalDate date = LocalDate.parse(day);
        return employeeRepository.countByHiredAt(date);
    }

    private int countUsersByRole(String roleName) {
        return (int) keycloakUserService.getAllUsers().stream()
                .filter(user -> {
                    String id = (String) user.get("id");
                    return keycloakUserService.getUserRoles(id).contains(roleName);
                })
                .count();
    }
}
