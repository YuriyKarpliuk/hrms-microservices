package org.yuriy.hrms.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.entity.Employee.Gender;
import org.yuriy.hrms.entity.Employee.MaritalStatus;
import org.yuriy.hrms.entity.Employee;

import java.time.LocalDate;

public class EmployeeSpecification {

    public enum StringMatchType {
        EXACT, CONTAINS, STARTS_WITH, ENDS_WITH
    }

    public static Specification<Employee> firstNameMatches(String value, StringMatchType type) {
        return (root, query, cb) -> {
            String val = value.toLowerCase();
            return switch (type) {
                case EXACT -> cb.equal(cb.lower(root.get("firstName")), val);
                case CONTAINS -> cb.like(cb.lower(root.get("firstName")), "%" + val + "%");
                case STARTS_WITH -> cb.like(cb.lower(root.get("firstName")), val + "%");
                case ENDS_WITH -> cb.like(cb.lower(root.get("firstName")), "%" + val);
            };
        };
    }

    public static Specification<Employee> lastNameMatches(String value, StringMatchType type) {
        return (root, query, cb) -> {
            String val = value.toLowerCase();
            return switch (type) {
                case EXACT -> cb.equal(cb.lower(root.get("lastName")), val);
                case CONTAINS -> cb.like(cb.lower(root.get("lastName")), "%" + val + "%");
                case STARTS_WITH -> cb.like(cb.lower(root.get("lastName")), val + "%");
                case ENDS_WITH -> cb.like(cb.lower(root.get("lastName")), "%" + val);
            };
        };
    }

    public static Specification<Employee> emailMatches(String value, StringMatchType type) {
        return (root, query, cb) -> {
            String val = value.toLowerCase();
            return switch (type) {
                case EXACT -> cb.equal(cb.lower(root.get("email")), val);
                case CONTAINS -> cb.like(cb.lower(root.get("email")), "%" + val + "%");
                case STARTS_WITH -> cb.like(cb.lower(root.get("email")), val + "%");
                case ENDS_WITH -> cb.like(cb.lower(root.get("email")), "%" + val);
            };
        };
    }


    public static Specification<Employee> hasStatus(Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }


    public static Specification<Employee> hasGender(Gender gender) {
        return (root, query, cb) ->
                gender == null ? null : cb.equal(root.get("gender"), gender);
    }

    public static Specification<Employee> hasMaritalStatus(MaritalStatus maritalStatus) {
        return (root, query, cb) ->
                maritalStatus == null ? null : cb.equal(root.get("maritalStatus"), maritalStatus);
    }

    public static Specification<Employee> hiredBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("hiredAt"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("hiredAt"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("hiredAt"), end);
            }
            return null;
        };
    }


    public static Specification<Employee> firedBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("terminatedAt"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("terminatedAt"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("terminatedAt"), end);
            }
            return null;
        };
    }

    public static Specification<Employee> hasBirthdayBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("birthDate"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("birthDate"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("birthDate"), end);
            }
            return null;
        };
    }

    public static Specification<Employee> hasOfficeLocation(String officeLocation) {
        return (root, query, cb) ->
                officeLocation == null ? null : cb.equal(root.get("officeLocation"), officeLocation);
    }

    public static Specification<Employee> inDepartment(Long deptId) {
        return (root, query, cb) ->
                deptId == null ? null : cb.equal(root.get("deptId"), deptId);
    }

    public static Specification<Employee> hasManager(Long managerId) {
        return (root, query, cb) ->
                managerId == null ? null : cb.equal(root.get("managerId"), managerId);
    }

    public static Specification<Employee> hasHR(Long hrId) {
        return (root, query, cb) ->
                hrId == null ? null : cb.equal(root.get("hrId"), hrId);
    }

    public static Specification<Employee> hasPhone(String phone) {
        return (root, query, cb) ->
                phone == null ? null : cb.equal(root.get("phone"), phone);
    }
}
