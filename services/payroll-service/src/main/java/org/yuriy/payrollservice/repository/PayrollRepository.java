package org.yuriy.payrollservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.payrollservice.entity.Payroll;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface PayrollRepository
        extends JpaRepository<Payroll, Long>, JpaSpecificationExecutor<Payroll> {
    List<Payroll> findByEmployeeId(Long employeeId);

    Optional<Payroll> findByEmployeeIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            Long employeeId,
            LocalDate start,
            LocalDate end
    );
}
