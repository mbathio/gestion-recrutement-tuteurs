package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.Candidature.StatutCandidature;
import com.uvs.recrutment.models.Candidat;
import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.repositories.CandidatureRepository;
import com.uvs.recrutment.repositories.CandidatRepository;
import com.uvs.recrutment.repositories.AnnonceRepository;
import com.uvs.recrutment.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/candidatures")
@CrossOrigin(origins = "*") // Autorise CORS pour Angular
public class CandidatureController {

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public Candidature createCandidature(@RequestBody Candidature candidature) {
        // Ici tu peux traiter et enregistrer la candidature dans ta base de données
        // Par exemple, on la retourne simplement pour l'instant
        return candidature;
    }
    /**
     * Soumettre une nouvelle candidature
     */
    @PostMapping("/soumettre")
    public ResponseEntity<?> soumettreCandidature(@RequestHeader("Authorization") String token,
                                                  @RequestBody Map<String, Long> payload) {
        try {
            // Extraire l'email du token
            String email = jwtUtil.extractUsername(token.substring(7)); // Supprime "Bearer "
            Optional<Candidat> candidatOpt = candidatRepository.findByEmail(email);

            if (candidatOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non autorisé ou non trouvé"));
            }

            Candidat candidat = candidatOpt.get();
            Long annonceId = payload.get("annonceId");
            Optional<Annonce> annonceOpt = annonceRepository.findById(annonceId);

            if (annonceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Annonce non trouvée"));
            }

            Candidature candidature = new Candidature();
            candidature.setCandidat(candidat);
            candidature.setAnnonce(annonceOpt.get());
            candidature.setStatut(StatutCandidature.EN_COURS);
            candidature.setDateSoumission(new Date()); 

            // Sauvegarde
            candidatureRepository.save(candidature);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Candidature soumise avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la soumission de la candidature"));
        }
    }

    /**
     * Récupérer toutes les candidatures d'un candidat
     */
    @GetMapping("/candidat")
    public ResponseEntity<?> getCandidatures(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractUsername(token.substring(7)); 
            Optional<Candidat> candidatOpt = candidatRepository.findByEmail(email);

            if (candidatOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilisateur non autorisé"));
            }

            Candidat candidat = candidatOpt.get();
            List<Candidature> candidatures = candidatureRepository.findByCandidat(candidat);

            return ResponseEntity.ok(candidatures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la récupération des candidatures"));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFichier(@RequestParam("file") MultipartFile file) {
        // Gérer le fichier (vérification du type, taille, enregistrement dans un répertoire, etc.)
        return ResponseEntity.ok("Fichier téléchargé avec succès");
    }

    /**
     * Modifier le statut d'une candidature (Admin uniquement)
     */
    @PutMapping("/statut/{id}")
    public ResponseEntity<?> changerStatut(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        try {
            Optional<Candidature> candidatureOpt = candidatureRepository.findById(id);

            if (candidatureOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Candidature non trouvée"));
            }

            String statutStr = payload.get("statut");
            Candidature candidature = candidatureOpt.get();

            try {
                StatutCandidature statut = StatutCandidature.valueOf(statutStr.toUpperCase());
                candidature.setStatut(statut);

                // Vérifier si un motif de refus est nécessaire
                if (statut == StatutCandidature.REFUSE) {
                    String motif = payload.get("motifRefus");
                    if (motif == null || motif.trim().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Un motif de refus est requis"));
                    }
                    candidature.setMotifRefus(motif);
                } else {
                    candidature.setMotifRefus(null);
                }

                candidatureRepository.save(candidature);
                return ResponseEntity.ok(Map.of("message", "Statut mis à jour avec succès"));

            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Statut invalide"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la mise à jour du statut"));
        }
    }

    @GetMapping("/list")
public ResponseEntity<Page<Candidature>> getCandidaturesPage(
        @RequestParam int page, @RequestParam int size) {
    try {
        Pageable pageable = PageRequest.of(page, size);
        Page<Candidature> candidatures = candidatureRepository.findAll(pageable);
        
        return ResponseEntity.ok(candidatures);
    } catch (Exception e) {
        // For error handling, you should return an empty page or some default page
        Page<Candidature> emptyPage = Page.empty();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyPage);
    }
}

    }
