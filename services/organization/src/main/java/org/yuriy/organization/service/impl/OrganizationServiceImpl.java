package org.yuriy.organization.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.organization.dto.mapper.OrganizationMapper;
import org.yuriy.organization.dto.request.OrganizationCreateRequest;
import org.yuriy.organization.dto.request.OrganizationPatchRequest;
import org.yuriy.organization.dto.request.OrganizationSearchRequest;
import org.yuriy.organization.dto.response.OrganizationResponse;
import org.yuriy.organization.entity.Organization;
import org.yuriy.organization.exception.ResourceNotFoundException;
import org.yuriy.organization.repository.OrganizationRepository;
import org.yuriy.organization.repository.specification.OrganizationSpecification;
import org.yuriy.organization.service.OrganizationService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
            OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public Page<OrganizationResponse> searchOrganizations(OrganizationSearchRequest request, Pageable pageable) {
        List<Specification<Organization>> specifications = new ArrayList<>();

        if (request.name() != null) {
            specifications.add(OrganizationSpecification.nameMatches(
                    request.name(), request.stringMatchType()
            ));
        }
        if (request.currency() != null) {
            specifications.add(OrganizationSpecification.hasCurrency(request.currency()));
        }

        if (request.taxNumber() != null) {
            specifications.add(OrganizationSpecification.hasTaxNumber(request.taxNumber()));
        }

        if (request.houseNumber() != null) {
            specifications.add(OrganizationSpecification.hasHouseNumber(request.houseNumber()));
        }

        if (request.street() != null) {
            specifications.add(OrganizationSpecification.hasStreet(request.street()));
        }

        if (request.city() != null) {
            specifications.add(OrganizationSpecification.hasCity(request.city()));
        }

        if (request.country() != null) {
            specifications.add(OrganizationSpecification.hasCountry(request.country()));
        }

        if (request.postalCode() != null) {
            specifications.add(OrganizationSpecification.hasPostalCode(request.postalCode()));
        }

        if (request.state() != null) {
            specifications.add(OrganizationSpecification.hasState(request.state()));
        }

        Specification<Organization> specification = Specification.allOf(specifications);

        return organizationRepository.findAll(specification, pageable)
                .map(organizationMapper::toResponse);
    }

    @Override
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll().stream().map(organizationMapper::toResponse).toList();
    }

    @Override
    public OrganizationResponse getOrganizationById(Long id) {
        return organizationRepository.findById(id).map(organizationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id " + id));
    }

    @Override
    @Transactional
    public OrganizationResponse createNewOrganization(OrganizationCreateRequest req) {
        if (organizationRepository.existsByName(req.name())) {
            throw new IllegalArgumentException("Organization with such name already exists");
        }
        var o = organizationMapper.toEntity(req);
        return organizationMapper.toResponse(organizationRepository.save(o));
    }

    @Override
    @Transactional
    public OrganizationResponse partialUpdateOrganization(Long id, OrganizationPatchRequest req) {
        var o = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id " + id));

        if (req.name() != null &&
                organizationRepository.existsByName(req.name())) {
            throw new IllegalArgumentException("Organization with such name already exists");
        }

        organizationMapper.applyPatch(o, req);
        return organizationMapper.toResponse(organizationRepository.save(o));
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    @Override
    public Boolean existsById(Long id) {
        return organizationRepository.existsById(id);
    }
}
