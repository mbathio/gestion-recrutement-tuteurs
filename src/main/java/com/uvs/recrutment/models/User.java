package com.uvs.recrutment.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data // Lombok générera automatiquement les getters, setters, toString(), equals(), hashCode(), etc.
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "role")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Candidat.class, name = "CANDIDAT"),
        @JsonSubTypes.Type(value = Administrateur.class, name = "ADMIN")
})
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        CANDIDAT, ADMIN
    }
}