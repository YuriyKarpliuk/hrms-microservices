package org.yuriy.timesheetservice.repository.specification;


import org.bouncycastle.util.Times;
import org.springframework.data.jpa.domain.Specification;
import org.yuriy.timesheetservice.entity.Timesheet;
import org.yuriy.timesheetservice.entity.TimesheetStatus;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class TimesheetSpecification {

    public static Specification<Timesheet> hasEmployee(Long employeeId) {
        return (root, query, cb) ->
                employeeId == null ? cb.conjunction() : cb.equal(root.get("employeeId"), employeeId);
    }

    public static Specification<Timesheet> startDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("weekStart"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("weekStart"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("weekStart"), to);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<Timesheet> endDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("weekEnd"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("weekEnd"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("weekEnd"), to);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<Timesheet> hasStatus(TimesheetStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Timesheet> employeeIn(List<Long> employeeIds) {
        return (root, query, cb) -> {
            if (employeeIds == null || employeeIds.isEmpty()) return cb.disjunction();
            return root.get("employeeId").in(employeeIds);
        };
    }

    public static Specification<Timesheet> startAfterOrEqual(LocalDate date) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("weekStart"), date);
    }

    public static Specification<Timesheet> endBeforeOrEqual(LocalDate date) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("weekEnd"), date);
    }
}
