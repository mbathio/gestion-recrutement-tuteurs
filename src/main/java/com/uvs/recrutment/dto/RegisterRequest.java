package com.uvs.recrutment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    private String password;


    @NotBlank(message = "Le numéro de téléphone est requis")
    @Pattern(regexp = "^(\\+?221|0)[76]\\d{8}$", message = "Format de numéro invalide")
    private String telephone;

    @NotBlank(message = "Le rôle est requis")
    private String role; // "ADMIN" ou "CANDIDAT"
}
