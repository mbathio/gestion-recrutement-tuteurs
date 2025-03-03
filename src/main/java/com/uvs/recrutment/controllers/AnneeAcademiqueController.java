package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.services.AnneeAcademiqueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils; // Ajout de l'import
import org.springframework.dao.DataIntegrityViolationException; // Ajout de l'import

// Reste du code inchangé

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/annees-academiques")
@CrossOrigin(origins = "http://localhost:4200")
public class AnneeAcademiqueController {

    private static final Logger logger = LoggerFactory.getLogger(AnneeAcademiqueController.class);

    @Autowired
    private AnneeAcademiqueService anneeAcademiqueService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CANDIDAT')")
    public ResponseEntity<?> getAllAnneesAcademiques(Authentication authentication) {
        try {
            logger.info("User {} requesting all academic years", 
                authentication != null ? authentication.getName() : "Unknown");
            
            List<AnneeAcademique> annees = anneeAcademiqueService.getAllAnneesAcademiques();
            
            if (annees.isEmpty()) {
                logger.warn("No academic years found");
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(annees);
        } catch (RuntimeException e) {
            logger.error("Error retrieving academic years", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Unable to retrieve academic years",
                    "details", e.getMessage(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown"
                ));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving academic years", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Unexpected error occurred",
                    "details", e.getMessage()
                ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAnneeAcademiqueById(@PathVariable Long id) {
        try {
            return anneeAcademiqueService.getAnneeAcademiqueById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            logger.error("Error retrieving academic year by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Unable to retrieve academic year",
                    "details", e.getMessage(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown"
                ));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving academic year by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Unexpected error occurred",
                    "details", e.getMessage()
                ));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createAnneeAcademique(
        @RequestBody AnneeAcademique anneeAcademique, 
        Authentication authentication
    ) {
        try {
            // Vérification explicite de l'authentification
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "Authentication required", 
                        "message", "Vous devez être connecté en tant qu'administrateur"
                    ));
            }

            // Récupération du nom d'utilisateur
            String username = authentication.getName();
            logger.info("Utilisateur {} tente de créer une année académique", username);

            // Validation de base
            if (anneeAcademique == null || StringUtils.isEmpty(anneeAcademique.getAnnee())) {
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error", "Données invalides", 
                        "message", "L'année académique ne peut pas être vide"
                    ));
            }

            // Création de l'année académique
            AnneeAcademique savedAnnee = anneeAcademiqueService.createAnneeAcademique(anneeAcademique);
            
            logger.info("Année académique {} créée avec succès", savedAnnee.getAnnee());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAnnee);

        } catch (DataIntegrityViolationException e) {
            logger.error("Erreur d'intégrité des données", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "error", "Conflit de données", 
                    "message", "Cette année académique existe déjà"
                ));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création de l'année académique", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Erreur serveur", 
                    "message", "Une erreur est survenue lors de la création",
                    "details", e.getMessage()
                ));
        }
    }
}
