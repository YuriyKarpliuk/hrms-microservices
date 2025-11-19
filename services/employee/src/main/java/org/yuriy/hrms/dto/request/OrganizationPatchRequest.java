package org.yuriy.hrms.dto.request;

public record OrganizationPatchRequest(
        String name,
        String website,
        String currency,
        String taxNumber
) {}
