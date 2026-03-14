package com.tfm.bandas.users.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK Interna autogenerada

    @Column(name = "iam_id", unique = true, nullable = false)
    @NotNull
    private String iamId; // ID del usuario en el sistema de autenticación externo (claims sub)

    @Column(name = "username", unique = true, nullable = false)
    @NotNull
    private String username; // Nombre de usuario único dentro del sistema

    @Column(name = "first_name", nullable = false)
    @NotNull
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "second_last_name")
    private String secondLastName;

    @Column(name = "email", unique = true, nullable = false)
    @Email
    @NotNull
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "band_join_date")
    private LocalDate bandJoinDate;       // Fecha en la que se unió a la banda

    @Column(name = "system_signup_date")
    private LocalDate systemSignupDate;   // Fecha de alta en el sistema

    @Column(name = "active", nullable = false)
    @NotNull
    private boolean active;

    @Column(name = "phone")
    private String phone;

    @Column(name = "notes")
    private String notes; // Notas adicionales sobre el usuario

    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // URL de la foto de perfil del usuario

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_profile_instrument",
        joinColumns = @JoinColumn(name = "user_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private Set<InstrumentEntity> instruments = new HashSet<>();

    // String informativo con los roles asignados al usuario
    @Column(name = "role_names")
    private String roleNames;

    @Version
    @Column(name = "version")
    private Integer version;

    public UserProfileEntity(UserProfileEntity userProfileEntity) {
        this.id = userProfileEntity.id;
        this.iamId = userProfileEntity.iamId;
        this.username = userProfileEntity.username;
        this.firstName = userProfileEntity.firstName;
        this.lastName = userProfileEntity.lastName;
        this.secondLastName = userProfileEntity.secondLastName;
        this.email = userProfileEntity.email;
        this.birthDate = userProfileEntity.birthDate;
        this.bandJoinDate = userProfileEntity.bandJoinDate;
        this.systemSignupDate = userProfileEntity.systemSignupDate;
        this.active = userProfileEntity.active;
        this.phone = userProfileEntity.phone;
        this.notes = userProfileEntity.notes;
        this.profilePictureUrl = userProfileEntity.profilePictureUrl;
        this.instruments = new HashSet<>(userProfileEntity.instruments);
        this.roleNames = userProfileEntity.roleNames;
    }
}