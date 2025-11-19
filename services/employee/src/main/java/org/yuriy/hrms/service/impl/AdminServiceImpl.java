package org.yuriy.hrms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yuriy.hrms.dto.request.*;
import org.yuriy.hrms.dto.response.AdminDashboardResponse;
import org.yuriy.hrms.dto.response.PageResponse;
import org.yuriy.hrms.service.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final OrganizationClient organizationClient;
    private final DepartmentClient departmentClient;
    private final LeaveClient leaveClient;
    private final EmployeeService employeeService;
    private final KeycloakUserService keycloakUserService;


    @Override
    public List<OrganizationClient.OrganizationResponse> getOrganizations() {
        return organizationClient.getAll();
    }

    @Override
    public OrganizationClient.OrganizationResponse createOrganization(OrganizationCreateRequest req) {
        return organizationClient.create(req);
    }

    @Override
    public OrganizationClient.OrganizationResponse updateOrganization(Long id, OrganizationPatchRequest req) {
        return organizationClient.update(id, req);
    }

    @Override
    public void deleteOrganization(Long id) {
        organizationClient.delete(id);
    }

    @Override
    public PageResponse<OrganizationClient.OrganizationResponse> searchOrganizations(OrganizationSearchRequest req, int page, int size) {
        return organizationClient.search(req, page, size);
    }


    @Override
    public List<DepartmentClient.DepartmentResponse> getDepartments() {
        return departmentClient.getAll();
    }

    @Override
    public List<DepartmentClient.DepartmentResponse> getDepartmentsByOrg(Long orgId) {
        return departmentClient.getByOrganization(orgId);
    }

    public PageResponse<DepartmentClient.DepartmentResponse> searchDepartments(
            DepartmentSearchRequest req, int page, int size
    ) {
        return departmentClient.search(req, page, size);
    }


    @Override
    public DepartmentClient.DepartmentResponse createDepartment(DepartmentCreateRequest req) {
        return departmentClient.create(req);
    }

    @Override
    public DepartmentClient.DepartmentResponse updateDepartment(Long id, DepartmentPatchRequest req) {
        return departmentClient.update(id, req);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentClient.delete(id);
    }

    @Override
    public AdminDashboardResponse getDashboard() {

        var employees = employeeService.getAllEmployees();
        var deps = departmentClient.getAll();
        var orgs = organizationClient.getAll();

        var metrics = new AdminDashboardResponse.Metrics(
                employees.size(),
                orgs.size(),
                deps.size()
        );

                var topOrganizations = orgs.stream()
                .map(o -> new AdminDashboardResponse.OrgCount(
                        o.name(),
                        employees.stream().filter(e -> e.orgId().equals(o.id())).count()
                ))
                .sorted(Comparator.comparingLong(AdminDashboardResponse.OrgCount::count).reversed())
                .limit(5)
                .toList();

        var topDepartments = deps.stream()
                .map(d -> new AdminDashboardResponse.DeptCount(
                        d.name(),
                        employees.stream().filter(e -> d.id().equals(e.deptId())).count()
                ))
                .sorted(Comparator.comparingLong(AdminDashboardResponse.DeptCount::count).reversed())
                .limit(5)
                .toList();

        long male = employees.stream().filter(e -> "MALE".equals(e.gender().toString())).count();
        long female = employees.stream().filter(e -> "FEMALE".equals(e.gender().toString())).count();

        var startOfMonth = LocalDate.now().withDayOfMonth(1);
        int newEmployeesThisMonth = (int) employees.stream()
                .filter(e -> e.hiredAt() != null && e.hiredAt().isAfter(startOfMonth.minusDays(1)))
                .count();
        List<Integer> roleStats = calculateRoleDistribution();
        AdminDashboardResponse.NewAccounts newAccounts = getNewAccountsLast30Days();

        var genderStats = new AdminDashboardResponse.GenderStats(male, female);

        return new AdminDashboardResponse(
                metrics,
                topOrganizations,
                topDepartments,
                genderStats,
                newEmployeesThisMonth,
                roleStats,
                newAccounts
        );
    }
    public List<Integer> calculateRoleDistribution() {

        var allUsers = keycloakUserService.getAllUsers();

        int admins = 0, hr = 0, managers = 0, users = 0;

        for (var u : allUsers) {
            String id = (String) u.get("id");
            var roles = keycloakUserService.getUserRoles(id);

            if (roles.contains("ADMIN")) admins++;
            if (roles.contains("HR")) hr++;
            if (roles.contains("MANAGER")) managers++;
            if (roles.contains("USER")) users++;
        }

        return List.of(admins, hr, managers, users);
    }

    public AdminDashboardResponse.NewAccounts getNewAccountsLast30Days() {

        var users = keycloakUserService.getAllUsers();

        LocalDate today = LocalDate.now();

        List<String> days = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {

            LocalDate day = today.minusDays(i);
            String label = day.format(DateTimeFormatter.ofPattern("MM-dd"));

            int count = 0;

            for (var u : users) {

                Object tsObj = u.get("createdTimestamp");
                if (tsObj == null) continue;

                long ts;

                if (tsObj instanceof Long l) {
                    ts = l;
                } else {
                    ts = Long.parseLong(tsObj.toString());
                }

                LocalDate created = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (created.equals(day)) {
                    count++;
                }
            }

            days.add(label);
            values.add(count);
        }

        return new AdminDashboardResponse.NewAccounts(days, values);
    }


}
