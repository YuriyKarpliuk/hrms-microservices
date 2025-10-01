package org.yuriy.organization.dto.request;


import java.util.List;

public record OrganizationPatchRequest(String name,
        String website,
        String currency,
        String taxNumber,
        List<OrganizationAddressRequest> addresses) {
}
