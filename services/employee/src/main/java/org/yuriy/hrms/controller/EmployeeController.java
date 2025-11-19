package org.yuriy.hrms.controller;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yuriy.hrms.dto.request.EmployeeCreateRequest;
import org.yuriy.hrms.dto.request.EmployeePatchRequest;
import org.yuriy.hrms.dto.request.EmployeeSearchRequest;
import org.yuriy.hrms.dto.response.*;
import org.yuriy.hrms.service.EmployeeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/managers")
    public ResponseEntity<List<EmployeeBasicResponse>> getAllManagers() {
        return ResponseEntity.ok(employeeService.getManagers());
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeFullResponse> getCurrentUserInfo(JwtAuthenticationToken token) {
        String email = token.getToken().getClaimAsString("email");

        return ResponseEntity.ok(employeeService.getByEmail(email));
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<EmployeeResponse> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(employeeService.uploadAvatar(id, file));
    }

    @PostMapping("/{id}/cv")
    public ResponseEntity<EmployeeFullResponse> uploadCv(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(employeeService.uploadCv(id, file));
    }

    @GetMapping("/{id}/cv/download")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long id) throws IOException {
        Resource resource = employeeService.downloadCv(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv_" + id + ".pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(resource);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<EmployeeResponse> createNewEmployee(@Valid @RequestBody EmployeeCreateRequest req) {
        return new ResponseEntity<>(employeeService.createNewEmployee(req), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> employeeFullUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeeCreateRequest req) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponse> employeePartialUpdate(@PathVariable Long id,
            @Valid @RequestBody EmployeePatchRequest req) {
        return ResponseEntity.ok(employeeService.patch(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search/{employeeId}")
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            @PathVariable Long employeeId,
            @RequestBody EmployeeSearchRequest request,
            @PageableDefault(sort = "email") Pageable pageable) {
        return ResponseEntity.ok(employeeService.searchEmployees(employeeId, request, pageable));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.existsById(id));
    }

    @GetMapping("/{id}/basic")
    public ResponseEntity<EmployeeBasicResponse> getEmployeeBasicInfo(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getBasicInfo(id));
    }

    @GetMapping("/find-id-by-email")
    public ResponseEntity<?> findEmployeeIdByEmail(@RequestParam String email) {
        return employeeService.findEmployeeIdByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/dashboard/{employeeId}")
    @PreAuthorize("hasRole('USER') or @employeeSecurity.isOwner(authentication)")
    public ResponseEntity<UserDashboardResponse> getUserDashboard(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeService.getDashboard(employeeId));
    }

    @GetMapping("/{managerId}/team")
    public ResponseEntity<List<EmployeeBasicResponse>> getEmployeesByManager(
            @PathVariable Long managerId
    ) {
        List<EmployeeBasicResponse> team = employeeService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(team);
    }
    @GetMapping("/manager/dashboard/{managerId}")
    public ResponseEntity<ManagerDashboardResponse> getDashboard(@PathVariable Long managerId) {
        return ResponseEntity.ok(employeeService.buildManagerDashboard(managerId));
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByOrganization(@PathVariable Long orgId) {
        return ResponseEntity.ok(employeeService.getEmployeesByOrganization(orgId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(departmentId));
    }

    @GetMapping("/org/{orgId}/birthdays/today")
    public ResponseEntity<List<EmployeeResponse>> getTodayBirthdaysForOrg(
            @PathVariable Long orgId
    ) {
        return ResponseEntity.ok(employeeService.getTodayBirthdaysForOrg(orgId));
    }




}
