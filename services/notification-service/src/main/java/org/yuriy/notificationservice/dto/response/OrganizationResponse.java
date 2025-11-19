package org.yuriy.notificationservice.dto.response;


public record OrganizationResponse(Long id,
        String name,
        String website,
        String currency,
        String taxNumber) {
}

