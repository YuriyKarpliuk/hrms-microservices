package org.yuriy.hrms.dto.request;
public record OrganizationSearchRequest(String name, StringMatchType stringMatchType,
        String currency, String taxNumber, String country, String postalCode, String state, String city, String street,
        String houseNumber) {
    public enum StringMatchType {
        EXACT, CONTAINS, STARTS_WITH, ENDS_WITH
    }

}
