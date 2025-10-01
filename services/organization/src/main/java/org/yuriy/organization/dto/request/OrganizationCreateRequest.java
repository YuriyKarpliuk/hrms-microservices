package org.yuriy.organization.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrganizationCreateRequest(@NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Website is required") String website,
        @NotBlank(message = "Currency is required") String currency,
        @NotBlank(message = "Tax number is required") String taxNumber,
        @NotEmpty(message = "At least one address is required")
        @Valid
        List<OrganizationAddressRequest> addresses) {
}

