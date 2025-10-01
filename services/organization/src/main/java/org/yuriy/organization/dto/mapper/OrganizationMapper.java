package org.yuriy.organization.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.organization.dto.request.OrganizationCreateRequest;
import org.yuriy.organization.dto.request.OrganizationPatchRequest;
import org.yuriy.organization.dto.response.OrganizationAddressResponse;
import org.yuriy.organization.dto.response.OrganizationResponse;
import org.yuriy.organization.entity.Organization;
import org.yuriy.organization.entity.OrganizationAddress;

import java.util.stream.Collectors;

@Component
public class OrganizationMapper {


    public Organization toEntity(OrganizationCreateRequest r) {
        Organization organization = new Organization();
        organization.setName(r.name());
        organization.setWebsite(r.website());
        organization.setCurrency(r.currency());
        organization.setTaxNumber(r.taxNumber());

        if (r.addresses() != null) {
            organization.setAddresses(r.addresses().stream()
                    .map(addr -> OrganizationAddress.builder().country(addr.country()).city(addr.city())
                            .street(addr.street()).postalCode(addr.zipCode()).organization(organization).build())
                    .toList());
        }

        return organization;
    }

    public void applyPatch(Organization o, OrganizationPatchRequest r) {
        if (r.name() != null)
            o.setName(r.name());
        if (r.website() != null)
            o.setWebsite(r.website());
        if (r.currency() != null)
            o.setCurrency(r.currency());
        if (r.taxNumber() != null)
            o.setTaxNumber(r.taxNumber());

        if (r.addresses() != null) {
            o.getAddresses().clear();
            o.getAddresses().addAll(r.addresses().stream()
                    .map(addr -> OrganizationAddress.builder().country(addr.country()).city(addr.city())
                            .street(addr.street()).postalCode(addr.zipCode()).organization(o).build())
                    .toList());
        }
    }

    public OrganizationResponse toResponse(Organization o) {
        return new OrganizationResponse(o.getId(), o.getName(), o.getWebsite(), o.getCurrency(), o.getTaxNumber(),
                o.getAddresses() != null ? o.getAddresses().stream()
                        .map(addr -> new OrganizationAddressResponse(addr.getId(), addr.getCountry(), addr.getCity(),
                                addr.getStreet(), addr.getPostalCode())).collect(Collectors.toList()) : null);
    }
}
