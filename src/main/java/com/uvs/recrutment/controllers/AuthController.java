package com.uvs.recrutment.controllers;

import com.uvs.recrutment.dto.AuthRequest;
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
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        try {
            // Vérifier si l'email existe déjà
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cet email est déjà utilisé"));
            }

            // Vérifier le rôle
            User.Role role;
            try {
                role = User.Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Rôle invalide. Utilisez 'ADMIN' ou 'CANDIDAT'"));
            }

            // Création du bon type d'utilisateur
            User newUser = (role == User.Role.CANDIDAT) ? new Candidat() : new Administrateur();

            // Remplir les informations utilisateur
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setNom(request.getNom());
            newUser.setPrenom(request.getPrenom());
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
            User user = existingUser.get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", user.getEmail(),
                    "nom", user.getNom(),
                    "prenom", user.getPrenom(),
                    "role", user.getRole().name()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }
}
