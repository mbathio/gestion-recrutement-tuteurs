package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.*;
import com.uvs.recrutment.repositories.CandidatureRepository;
import com.uvs.recrutment.repositories.UserRepository;
import com.uvs.recrutment.repositories.AnnonceRepository;
import com.uvs.recrutment.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/candidatures")
public class CandidatureController {

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Soumettre une candidature
    @PostMapping("/soumettre")
    public ResponseEntity<?> soumettreCandidature(@RequestHeader("Authorization") String token, 
                                                  @RequestBody Map<String, Long> payload) {
        try {
            // Extraire l'email du token
            String email = jwtUtil.extractUsername(token.substring(7)); // Supprimer le "Bearer " au début du token
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty() || user.get().getRole() != User.Role.CANDIDAT) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Utilisateur non autorisé à soumettre une candidature"));
            }

            // Récupérer l'annonce et le candidat
            Long annonceId = payload.get("annonceId");
            Optional<Annonce> annonce = annonceRepository.findById(annonceId);

            if (annonce.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Annonce non trouvée"));
            }

            Candidat candidat = (Candidat) user.get();
            Candidature candidature = new Candidature();
            candidature.setCandidat(candidat);
            candidature.setAnnonce(annonce.get());
            candidature.setStatut(Candidature.StatutCandidature.EN_COURS);
            candidature.setDateSoumission(new java.util.Date()); // Date de soumission

            // Sauvegarder la candidature en base de données
            candidatureRepository.save(candidature);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Candidature soumise avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erreur lors de la soumission de la candidature"));
        }
    }

    // Récupérer toutes les candidatures d'un candidat
    @GetMapping("/candidat")
    public ResponseEntity<?> getCandidatures(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractUsername(token.substring(7)); // Supprimer le "Bearer " au début du token
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty() || user.get().getRole() != User.Role.CANDIDAT) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Utilisateur non autorisé"));
            }

            Candidat candidat = (Candidat) user.get();
            return ResponseEntity.ok(candidat.getCandidature()); // Retourner la liste des candidatures du candidat
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erreur lors de la récupération des candidatures"));
        }
    }

    // Changer le statut d'une candidature (pour l'admin)
    @PutMapping("/statut/{id}")
    public ResponseEntity<?> changerStatut(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        try {
            Optional<Candidature> candidature = candidatureRepository.findById(id);

            if (candidature.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Candidature non trouvée"));
            }

            String statutStr = payload.get("statut");
            try {
                Candidature.StatutCandidature statut = Candidature.StatutCandidature.valueOf(statutStr.toUpperCase());
                candidature.get().setStatut(statut);
                candidatureRepository.save(candidature.get());
                return ResponseEntity.ok(Map.of("message", "Statut de la candidature mis à jour"));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Statut invalide"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erreur lors de la mise à jour du statut"));
        }
    }
}
