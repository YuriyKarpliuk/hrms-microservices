package org.yuriy.hrms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yuriy.hrms.entity.Employee;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailAndOrgId(String email, Long orgId);

    boolean existsByEmailAndOrgIdAndIdNot(String email, Long orgId, Long id);
}
