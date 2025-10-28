package com.tfm.bandas.usuarios.model.specification;

import com.tfm.bandas.usuarios.model.entity.UserProfileEntity;
import com.tfm.bandas.usuarios.model.entity.InstrumentEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<UserProfileEntity> usernameContains(String username) {
        return (root, query, cb) ->
                username == null ? null : cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<UserProfileEntity> firstNameContains(String firstName) {
        return (root, query, cb) ->
                firstName == null ? null : cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<UserProfileEntity> lastNameContains(String lastName) {
        return (root, query, cb) ->
                lastName == null ? null : cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<UserProfileEntity> secondLastNameContains(String secondLastName) {
        return (root, query, cb) ->
                secondLastName == null ? null : cb.like(cb.lower(root.get("secondLastName")), "%" + secondLastName.toLowerCase() + "%");
    }

    public static Specification<UserProfileEntity> emailContains(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<UserProfileEntity> activeIs(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<UserProfileEntity> hasInstrument(Long instrumentId) {
        return (root, query, cb) -> {
            if (instrumentId == null) return null;
            Join<UserProfileEntity, InstrumentEntity> instruments = root.join("instruments");
            return cb.equal(instruments.get("id"), instrumentId);
        };
    }
}
