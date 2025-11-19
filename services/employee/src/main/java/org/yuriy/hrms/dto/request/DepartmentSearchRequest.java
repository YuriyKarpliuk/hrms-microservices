package org.yuriy.hrms.dto.request;

public record DepartmentSearchRequest(String name, StringMatchType stringMatchType,
        Long managerId, Long orgId) {
    public enum StringMatchType {
        EXACT, CONTAINS, STARTS_WITH, ENDS_WITH
    }
}

