package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Candidat;
import com.uvs.recrutment.repositories.CandidatRepository;
import com.uvs.recrutment.security.JwtUtil;
import com.uvs.recrutment.services.CandidatService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    private final JwtUtil jwtUtil;
    private final CandidatRepository candidatRepository;
    private final CandidatService candidatService;

    // Injection des dépendances via le constructeur
    public CandidatController(JwtUtil jwtUtil, CandidatRepository candidatRepository, CandidatService candidatService) {
        this.jwtUtil = jwtUtil;
        this.candidatRepository = candidatRepository;
        this.candidatService = candidatService;
    }

    // Récupérer le candidat actuel via le token JWT
    @GetMapping("/candidat")
    public ResponseEntity<?> getCandidat(@RequestHeader("Authorization") String token) {
        try {
            // Extrait l'email à partir du token JWT
            String email = jwtUtil.extractUsername(token.substring(7)); // Supprime "Bearer "
            
            // Recherche le candidat par email
            Optional<Candidat> candidat = candidatRepository.findByEmail(email);

            if (candidat.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Candidat non trouvé"));
            }

            // Retourner les informations du candidat
            return ResponseEntity.ok(candidat.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Erreur de traitement du token"));
        }
    }

    // Liste des candidats pour l'administrateur
    @GetMapping("/all")
    public ResponseEntity<List<Candidat>> getAllCandidats() {
        try {
            List<Candidat> candidats = candidatService.getAllCandidats();
            return ResponseEntity.ok(candidats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @GetMapping("/candidats")
    public ResponseEntity<List<Candidat>> getAllCandidatsNew() {
        List<Candidat> candidats = candidatService.getAllCandidats();
        return ResponseEntity.ok(candidats);
    }
}
