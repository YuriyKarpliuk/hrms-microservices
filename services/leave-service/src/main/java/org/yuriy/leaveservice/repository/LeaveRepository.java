package org.yuriy.leaveservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yuriy.leaveservice.entity.Leave;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Repository
public interface LeaveRepository
        extends JpaRepository<Leave, Long>, JpaSpecificationExecutor<Leave> {
    List<Leave> findByEmployeeId(Long employeeId);

    @Query("SELECT COUNT(l) FROM Leave l WHERE l.employeeId = :employeeId " +
            "AND l.type = 'VACATION' AND YEAR(l.startDate) = :year")
    int countUsedVacationDays(Long employeeId, int year);

    List<Leave> findByEmployeeIdAndStartDateAfter(Long employeeId, LocalDate startDate);
}
