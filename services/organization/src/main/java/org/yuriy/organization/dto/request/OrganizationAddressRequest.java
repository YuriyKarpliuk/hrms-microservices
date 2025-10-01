package org.yuriy.organization.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrganizationAddressRequest(@NotBlank(message = "Country is required") String country,
        @NotBlank(message = "City is required") String city, @NotBlank(message = "Street is required") String street,
        @NotBlank(message = "ZipCode is required") String zipCode) {
}
