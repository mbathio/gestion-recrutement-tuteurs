package com.uvs.recrutment.controllers;

import com.uvs.recrutment.dto.RegisterRequest;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Vérifier si l'email existe déjà
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cet email est déjà utilisé"));
            }

            // Seuls les candidats peuvent s'inscrire
            Candidat candidat = new Candidat();
            candidat.setNom(request.getNom());             // Enregistrer le nom
            candidat.setPrenom(request.getPrenom());       // Enregistrer le prénom
            candidat.setEmail(request.getEmail());         // Enregistrer l'email
            candidat.setTelephone(request.getTelephone()); // Enregistrer le téléphone
            candidat.setPassword(passwordEncoder.encode(request.getPassword()));
            candidat.setRole(User.Role.CANDIDAT);

            // Sauvegarde en base de données
            userRepository.save(candidat);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Candidat enregistré avec succès !",
                    "nom", candidat.getNom(),
                    "prenom", candidat.getPrenom(),
                    "email", candidat.getEmail(),
                    "telephone", candidat.getTelephone()
            ));
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
                    "telephone", user instanceof Candidat ? ((Candidat) user).getTelephone() : "",
                    "role", user.getRole().name()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }
}
