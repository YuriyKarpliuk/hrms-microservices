package org.yuriy.payrollservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.payrollservice.entity.Payroll;


@Repository
public interface PayrollRepository
        extends JpaRepository<Payroll, Long>, JpaSpecificationExecutor<Payroll> {
}
