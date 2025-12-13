package com.tfm.bandas.users.model.specification;

import com.tfm.bandas.users.model.entity.UserProfileEntity;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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

    // Specification para filtrar usuarios que tienen un rol específico utilizando el campo role_names de UserProfileEntity
    public static Specification<UserProfileEntity> hasRole(String roleName) {
        return (root, query, cb) ->
                roleName == null ? null : cb.like(cb.upper(root.get("roleNames")), "%" + roleName.toUpperCase() + "%");
    }


    public static Specification<UserProfileEntity> birthDateBetween(LocalDate birthDateFrom, LocalDate birthDateTo) {
        return dateBetween("birthDate", birthDateFrom, birthDateTo);
    }

    public static Specification<UserProfileEntity> bandJoinDateBetween(LocalDate bandJoinDateFrom, LocalDate bandJoinDateTo) {
        return dateBetween("bandJoinDate", bandJoinDateFrom, bandJoinDateTo);
    }

    // Metodo común para rangos de fechas
    private static Specification<UserProfileEntity> dateBetween(String attributeName, LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            LocalDate toDate = to;
            // Si solo hay "from", la fecha "to" es hoy
            if (from != null && toDate == null) {
                toDate = LocalDate.now();
            }
            // Si solo hay "to", no hay límite inferior
            if (from == null) {
                return cb.lessThanOrEqualTo(root.get(attributeName), toDate);
            }
            // Si hay ambos, busca en el rango
            return cb.between(root.get(attributeName), from, toDate);
        };
    }
}
