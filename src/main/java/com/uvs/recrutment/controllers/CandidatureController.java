package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.Candidature.StatutCandidature;
import com.uvs.recrutment.models.Candidat;
import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.services.CandidatureService;
import com.uvs.recrutment.repositories.CandidatRepository;
import com.uvs.recrutment.repositories.AnnonceRepository;
import com.uvs.recrutment.repositories.CandidatureRepository;
import com.uvs.recrutment.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/candidatures")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CandidatureController {

    @Autowired
    private CandidatureService candidatureService;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @PostMapping("/soumettre")
    public ResponseEntity<?> soumettreCandidature(@RequestHeader("Authorization") String token,
                                                  @RequestBody Map<String, Long> payload) {
        try {
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

            // Créer la candidature et la soumettre
            Candidature candidature = new Candidature();
            candidature.setCandidat(candidat);
            candidature.setAnnonce(annonceOpt.get());
            candidature.setStatut(Candidature.StatutCandidature.EN_COURS); // Statut initial "EN_COURS"
            candidature.setDateSoumission(new Date());

            // Utilisation du service pour la sauvegarde
            candidatureService.createCandidature(candidature);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Candidature soumise avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la soumission de la candidature"));
        }
    }

    @PutMapping("/{id}/status")
public ResponseEntity<?> changerStatut(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
    try {
        // Vérifier si la candidature existe
        Optional<Candidature> candidatureOpt = candidatureRepository.findById(id);
        if (candidatureOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Candidature non trouvée"));
        }

        Candidature candidature = candidatureOpt.get();
        String statutStr = payload.get("statut");
        String motifRefus = payload.get("motifRefus");

        // Vérification si statutStr est null ou vide
        if (statutStr == null || statutStr.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Le statut ne peut pas être vide ou null"));
        }

        // Conversion du statut en Enum en gérant les erreurs
        StatutCandidature statut;
        try {
            statut = Candidature.getStatutFromString(statutStr); // Utilisation de la méthode pour obtenir le statut
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Statut invalide", "error", e.getMessage()));
        }

        // Mise à jour du statut et du motif de refus
        candidature.setStatut(statut);
        candidature.setMotifRefus(motifRefus);
        candidature.setDateDerniereModification(new Date());

        // Enregistrer la candidature mise à jour
        Candidature updatedCandidature = candidatureRepository.save(candidature);

        return ResponseEntity.ok(Map.of("message", "Statut mis à jour avec succès", "candidature", updatedCandidature));
    } catch (Exception e) {
        // Logguer l'exception pour faciliter le debug
        e.printStackTrace();  // Cela va vous permettre de voir l'erreur exacte dans les logs du serveur
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la mise à jour du statut", "error", e.getMessage()));
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
            // Pour la gestion des erreurs, il serait utile de renvoyer une page vide ou une page par défaut
            Page<Candidature> emptyPage = Page.empty();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyPage);
        }
    }
}
