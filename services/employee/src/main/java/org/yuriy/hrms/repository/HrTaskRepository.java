package org.yuriy.hrms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yuriy.hrms.entity.HrTask;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface HrTaskRepository extends JpaRepository<HrTask, Long> {

    List<HrTask> findByCreatedByOrderByCreatedAtDesc(Long hrId);

    List<HrTask> findByEmployeeIdOrderByCreatedAtDesc(Long empId);

    List<HrTask> findAllByStatusNotAndDueDateBefore(
            HrTask.TaskStatus status, LocalDate date);

    @Query("""
        SELECT t FROM HrTask t
        JOIN Employee e ON e.id = t.employeeId
        WHERE e.orgId = :orgId AND t.status = :status
        ORDER BY t.dueDate ASC
    """)
    List<HrTask> findTopTasks(Long orgId, HrTask.TaskStatus status);


    @Query("""
        SELECT COUNT(t) FROM HrTask t
        JOIN Employee e ON e.id = t.employeeId
        WHERE e.orgId = :orgId AND t.status = :status
    """)
    int countByOrgIdAndStatus(Long orgId, HrTask.TaskStatus status);


    @Query("""
        SELECT COUNT(t) FROM HrTask t
        JOIN Employee e ON e.id = t.employeeId
        WHERE e.orgId = :orgId 
          AND t.type = :type
          AND t.status = :status
    """)
    int countByOrgIdAndTypeAndStatus(
            Long orgId,
            HrTask.TaskType type,
            HrTask.TaskStatus status
    );
}
