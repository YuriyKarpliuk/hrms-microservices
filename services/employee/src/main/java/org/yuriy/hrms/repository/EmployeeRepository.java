package org.yuriy.hrms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yuriy.hrms.entity.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByOrgIdAndStatus(Long orgId, Employee.Status status);

    boolean existsByEmailAndOrgId(String email, Long orgId);

    boolean existsByEmailAndOrgIdAndIdNot(String email, Long orgId, Long id);
}
