package org.yuriy.timesheetservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.timesheetservice.entity.Timesheet;

import java.util.List;


@Repository
public interface TimesheetRepository
        extends JpaRepository<Timesheet, Long>, JpaSpecificationExecutor<Timesheet> {
    List<Timesheet> findByEmployeeId(Long employeeId);
}
