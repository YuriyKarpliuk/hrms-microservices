package org.yuriy.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.department.entity.Department;

import java.util.Collection;
import java.util.List;


@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    boolean existsByNameAndOrgId(String name, Long orgId);

    List<Department> findByOrgId(Long orgId);
}
