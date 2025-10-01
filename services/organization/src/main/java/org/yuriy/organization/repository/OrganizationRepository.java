package org.yuriy.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.yuriy.organization.entity.Organization;


@Repository
public interface OrganizationRepository
        extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
    boolean existsByName(String name);
}
