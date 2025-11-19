package org.yuriy.timesheetservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yuriy.timesheetservice.entity.TimesheetEntry;

import java.util.List;

@Repository
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {
    List<TimesheetEntry> findByTimesheetId(Long timesheetId);
    void deleteByTimesheetId(Long timesheetId);
}
