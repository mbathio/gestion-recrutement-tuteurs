package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.*;
import com.uvs.recrutment.repositories.UserRepository;
import com.uvs.recrutment.security.JwtUtil;
import com.uvs.recrutment.dto.AuthResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Map<String, String> request) {
        try {
            logger.info("Received registration request: {}", request);
            
            String email = request.get("email");
            String password = request.get("password");
            String nom = request.get("nom");
            String prenom = request.get("prenom");
            String telephone= request.get("telephone");

            logger.info("Registration details - Email: {}, Nom: {}, Prenom: {}", email, nom, prenom);

            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Cet email est déjà utilisé", null, null));
            }

            // Example password strength check
            if (password.length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Le mot de passe doit comporter au moins 8 caractères", null, null));
            }

            Candidat candidat = new Candidat();
            candidat.setNom(nom);
            candidat.setPrenom(prenom);
            candidat.setEmail(email);
            candidat.setPassword(passwordEncoder.encode(password));
            candidat.setRole(User.Role.CANDIDAT);
            candidat.setTelephone(telephone); // Assurez-vous que le modèle a un setter pour le téléphone

            userRepository.save(candidat);

            String token = jwtUtil.generateToken(candidat.getEmail(), User.Role.CANDIDAT.name());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(token, "Inscription réussie", User.Role.CANDIDAT.name(), candidat.getId()));
        } catch (Exception e) {
            logger.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "Erreur lors de l'inscription", null, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "Email et mot de passe sont requis", null, null));
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            User user = existingUser.get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    "Connexion réussie",
                    user.getRole().name(),
                    user.getId()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Email ou mot de passe incorrect", null, null));
        }
    }
}