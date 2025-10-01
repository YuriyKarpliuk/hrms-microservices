package org.yuriy.department.repository.specification;


import org.springframework.data.jpa.domain.Specification;
import org.yuriy.department.entity.Department;

public class DepartmentSpecification {
    public enum StringMatchType {
        EXACT, CONTAINS, STARTS_WITH, ENDS_WITH
    }

    public static Specification<Department> nameMatches(String value, StringMatchType type) {
        return (root, query, cb) -> {
            String val = value.toLowerCase();
            return switch (type) {
                case EXACT -> cb.equal(cb.lower(root.get("name")), val);
                case CONTAINS -> cb.like(cb.lower(root.get("name")), "%" + val + "%");
                case STARTS_WITH -> cb.like(cb.lower(root.get("name")), val + "%");
                case ENDS_WITH -> cb.like(cb.lower(root.get("name")), "%" + val);
            };
        };
    }

    public static Specification<Department> hasManager(Long managerId) {
        return (root, query, cb) ->
                managerId == null ? cb.conjunction() : cb.equal(root.get("managerId"), managerId);
    }

    public static Specification<Department> inOrganization(Long orgId) {
        return (root, query, cb) ->
                orgId == null ? cb.conjunction() : cb.equal(root.get("orgId"), orgId);
    }

}
