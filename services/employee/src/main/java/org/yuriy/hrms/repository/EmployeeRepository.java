package org.yuriy.hrms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yuriy.hrms.entity.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByEmailAndOrgId(String email, Long orgId);

    boolean existsByEmailAndOrgIdAndIdNot(String email, Long orgId, Long id);

    Optional<Employee> findByUserId(String userId);

    Optional<Employee> findEmployeeByEmail(String email);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByManagerId(Long managerId);

    List<Employee> findByDeptId(Long deptId);

    int countByOrgIdAndStatus(Long orgId, Employee.Status status);

    int countByOrgId(Long orgId);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.orgId = :orgId AND e.hiredAt >= date_trunc('month', CURRENT_DATE)")
    int countHiredThisMonth(Long orgId);

    List<Employee> findByOrgId(Long orgId);

    int countByHiredAt(LocalDate date);
}
