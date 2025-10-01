package org.yuriy.leaveservice.repository.specification;


import org.springframework.data.jpa.domain.Specification;
import org.yuriy.leaveservice.entity.Leave;
import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;

import java.time.LocalDate;

public class LeaveSpecification {
    public static Specification<Leave> hasEmployeeId(Long employeeId) {
        return (root, query, cb) -> employeeId == null
                ? cb.conjunction()
                : cb.equal(root.get("employee").get("id"), employeeId);
    }

    public static Specification<Leave> hasStatus(LeaveStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Leave> hasType(LeaveType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Leave> startDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("startDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("startDate"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("startDate"), to);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Leave> endDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("endDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("endDate"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("endDate"), to);
            }
            return cb.conjunction();
        };
    }

}
