package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.services.CandidatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CandidatureService candidatureService;

    // Récupérer toutes les candidatures
    @GetMapping("/candidatures")
    public ResponseEntity<List<Candidature>> getAllCandidatures() {
        List<Candidature> candidatures = candidatureService.getAllCandidatures();
        return ResponseEntity.ok(candidatures);
    }

    // Récupérer une candidature par ID
    @GetMapping("/candidature/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable Long id) {
        Optional<Candidature> candidature = candidatureService.getCandidatureById(id);
        return candidature.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Créer une candidature
    @PostMapping("/candidature")
    public ResponseEntity<Candidature> createCandidature(@RequestBody Candidature candidature) {
        Candidature createdCandidature = candidatureService.createCandidature(candidature);
        return ResponseEntity.ok(createdCandidature);
    }

    // Supprimer une candidature
    @DeleteMapping("/candidature/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable Long id) {
        boolean deleted = candidatureService.deleteCandidature(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
