package org.yuriy.payrollservice.repository.specification;


import org.springframework.data.jpa.domain.Specification;
import org.yuriy.payrollservice.entity.Payroll;
import org.yuriy.payrollservice.entity.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollSpecification {

    public static Specification<Payroll> hasEmployee(Long employeeId) {
        return (root, query, cb) ->
                employeeId == null ? cb.conjunction() : cb.equal(root.get("employeeId"), employeeId);
    }

    public static Specification<Payroll> periodOverlaps(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("periodStart"), from),
                        cb.lessThanOrEqualTo(root.get("periodEnd"), to)
                );
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("periodStart"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("periodEnd"), to);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<Payroll> hasStatus(PayrollStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Payroll> netSalaryBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("netSalary"), BigDecimal.valueOf(min), BigDecimal.valueOf(max));
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("netSalary"), BigDecimal.valueOf(min));
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("netSalary"), BigDecimal.valueOf(max));
            } else {
                return cb.conjunction();
            }
        };
    }

}
