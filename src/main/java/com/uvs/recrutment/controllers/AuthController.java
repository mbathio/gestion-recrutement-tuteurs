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
public ResponseEntity<?> register(@Valid @RequestBody Map<String, String> request) {
    try {
        String email = request.get("email");
        String password = request.get("password");
        String nom = request.get("nom");
        String prenom = request.get("prenom");
        String telephone = request.get("telephone");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Cet email est déjà utilisé"));
        }

        // Example password strength check
        if (password.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Le mot de passe doit comporter au moins 8 caractères"));
        }

        Candidat candidat = new Candidat();
        candidat.setNom(nom);
        candidat.setPrenom(prenom);
        candidat.setEmail(email);
        candidat.setTelephone(telephone);
        candidat.setPassword(passwordEncoder.encode(password));
        candidat.setRole(User.Role.CANDIDAT);

        userRepository.save(candidat);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Inscription réussie",
                "id", candidat.getId(),
                "nom", candidat.getNom(),
                "prenom", candidat.getPrenom(),
                "email", candidat.getEmail(),
                "telephone", candidat.getTelephone()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Une erreur s'est produite lors de l'inscription"));
    }
}


@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
    String email = payload.get("email");
    String password = payload.get("password");

    if (email == null || email.isBlank() || password == null || password.isBlank()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Email et mot de passe sont requis"));
    }

    Optional<User> existingUser = userRepository.findByEmail(email);

    if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
        User user = existingUser.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", user.getId(),
                "email", user.getEmail(),
                "nom", user.getNom(),
                "prenom", user.getPrenom(),
                "telephone", user instanceof Candidat ? ((Candidat) user).getTelephone() : "",
                "role", user.getRole().name()
        ));
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Email ou mot de passe incorrect"));
    }
}
}