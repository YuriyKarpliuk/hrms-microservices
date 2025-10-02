package org.yuriy.organization.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yuriy.organization.dto.request.OrganizationCreateRequest;
import org.yuriy.organization.dto.request.OrganizationPatchRequest;
import org.yuriy.organization.dto.request.OrganizationSearchRequest;
import org.yuriy.organization.dto.response.OrganizationResponse;

import java.util.List;


public interface OrganizationService {

    Page<OrganizationResponse> searchOrganizations(OrganizationSearchRequest request, Pageable pageable);

    List<OrganizationResponse> getAllOrganizations();

    OrganizationResponse getOrganizationById(Long id);

    OrganizationResponse createNewOrganization(OrganizationCreateRequest req);

    OrganizationResponse partialUpdateOrganization(Long id, OrganizationPatchRequest req);

    void deleteOrganization(Long id);

    Boolean existsById(Long id);
}
