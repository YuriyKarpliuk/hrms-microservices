package org.yuriy.leaveservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.leaveservice.entity.Leave;

import java.util.List;


@Repository
public interface LeaveRepository
        extends JpaRepository<Leave, Long>, JpaSpecificationExecutor<Leave> {
    List<Leave> findByEmployeeId(Long employeeId);
}
