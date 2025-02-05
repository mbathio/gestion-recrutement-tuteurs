package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.*;
import com.uvs.recrutment.repositories.UserRepository;
import com.uvs.recrutment.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Map<String, Object> payload) {
        try {
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            String nom = (String) payload.get("nom");
            String prenom = (String) payload.get("prenom");
            String roleStr = (String) payload.get("role");

            // Vérification du rôle
            User.Role role;
            try {
                role = User.Role.valueOf(roleStr.toUpperCase()); // Convertit en majuscules pour éviter les erreurs
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Rôle invalide. Utilisez 'ADMIN' ou 'CANDIDAT'"));
            }

            // Vérifier si l'email existe déjà
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cet email est déjà utilisé"));
            }

            // Création du bon type d'utilisateur
            User newUser;
            if (role == User.Role.CANDIDAT) {
                newUser = new Candidat();
            } else {
                newUser = new Administrateur();
            }

            // Remplissage des informations utilisateur
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setRole(role);

            // Sauvegarde en base de données
            userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Utilisateur enregistré avec succès !"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Une erreur s'est produite lors de l'inscription"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            String token = jwtUtil.generateToken(email, existingUser.get().getRole().name());
            return ResponseEntity.ok(Map.of("token", token, "role", existingUser.get().getRole().name()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }
}