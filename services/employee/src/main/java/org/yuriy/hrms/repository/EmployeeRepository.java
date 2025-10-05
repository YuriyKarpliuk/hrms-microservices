package org.yuriy.hrms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.hrms.entity.Employee;

import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByEmailAndOrgId(String email, Long orgId);

    boolean existsByEmailAndOrgIdAndIdNot(String email, Long orgId, Long id);

    Optional<Employee> findByUserId(String userId);
}
