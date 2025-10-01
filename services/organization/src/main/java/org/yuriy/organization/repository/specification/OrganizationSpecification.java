package org.yuriy.organization.repository.specification;


import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.yuriy.organization.entity.Organization;
import org.yuriy.organization.entity.OrganizationAddress;

public class OrganizationSpecification {
    public enum StringMatchType {
        EXACT, CONTAINS, STARTS_WITH, ENDS_WITH
    }

    public static Specification<Organization> nameMatches(String value, StringMatchType type) {
        return (root, query, cb) -> {
            String val = value.toLowerCase();
            return switch (type) {
                case EXACT -> cb.equal(cb.lower(root.get("name")), val);
                case CONTAINS -> cb.like(cb.lower(root.get("name")), "%" + val + "%");
                case STARTS_WITH -> cb.like(cb.lower(root.get("name")), val + "%");
                case ENDS_WITH -> cb.like(cb.lower(root.get("name")), "%" + val);
            };
        };
    }

    public static Specification<Organization> hasCurrency(String currency) {
        return (root, query, cb) ->
                currency == null ? null : cb.equal(root.get("currency"), currency);
    }

    public static Specification<Organization> hasTaxNumber(String taxNumber) {
        return (root, query, cb) ->
                taxNumber == null ? null : cb.equal(root.get("taxNumber"), taxNumber);
    }

    private static Join<Organization, OrganizationAddress> joinAddress(
            Root<Organization> root,
            CriteriaQuery<?> query) {
        query.distinct(true);
        return root.join("addresses", JoinType.INNER);
    }

    public static Specification<Organization> hasHouseNumber(String houseNumber) {
        return (root, query, cb) -> {
            if (houseNumber == null)
                return null;
            assert query != null;
            Join<Organization, OrganizationAddress> join = joinAddress(root, query);
            return cb.equal(join.get("houseNumber"), houseNumber);
        };
    }

    public static Specification<Organization> hasStreet(String street) {
        return (root, query, cb) -> {
            if (street == null)
                return null;
            assert query != null;
            Join<Organization, OrganizationAddress> join = joinAddress(root, query);
            return cb.like(cb.lower(join.get("street")), "%" + street.toLowerCase() + "%");
        };
    }

    public static Specification<Organization> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null)
                return null;
            assert query != null;
            Join<Organization, OrganizationAddress> join = joinAddress(root, query);
            return cb.equal(join.get("city"), city);
        };
    }

    public static Specification<Organization> hasState(String state) {
        return (root, query, cb) -> {
            if (state == null)
                return null;
            assert query != null;
            Join<Organization, OrganizationAddress> join = joinAddress(root, query);
            return cb.equal(join.get("state"), state);
        };
    }

    public static Specification<Organization> hasPostalCode(String postalCode) {
        return (root, query, cb) -> {
            if (postalCode == null)
                return null;
            assert query != null;
            Join<Organization, OrganizationAddress> join = joinAddress(root, query);
            return cb.equal(join.get("postalCode"), postalCode);
        };
    }

    public static Specification<Organization> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null)
                return null;
            assert query != null;
            Join<Organization, OrganizationAddress> join = joinAddress(root, query);
            return cb.equal(join.get("country"), country);
        };
    }

}
