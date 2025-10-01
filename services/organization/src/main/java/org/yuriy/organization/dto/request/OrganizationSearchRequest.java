package org.yuriy.organization.dto.request;


import org.yuriy.organization.repository.specification.OrganizationSpecification;

public record OrganizationSearchRequest(String name, OrganizationSpecification.StringMatchType stringMatchType,
        String currency, String taxNumber, String country, String postalCode, String state, String city, String street,
        String houseNumber) {
}
