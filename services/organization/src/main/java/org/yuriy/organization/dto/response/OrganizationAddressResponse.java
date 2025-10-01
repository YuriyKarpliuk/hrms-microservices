package org.yuriy.organization.dto.response;

public record OrganizationAddressResponse(
        Long id,
        String country,
        String city,
        String street,
        String zipCode
) {
}
