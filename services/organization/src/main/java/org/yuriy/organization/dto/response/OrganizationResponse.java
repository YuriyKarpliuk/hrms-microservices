package org.yuriy.organization.dto.response;


import java.util.List;

public record OrganizationResponse(Long id,
        String name,
        String website,
        String currency,
        String taxNumber,
        List<OrganizationAddressResponse> addresses) {
}
