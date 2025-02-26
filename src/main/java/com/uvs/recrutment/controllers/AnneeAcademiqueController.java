package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.repositories.AnneeAcademiqueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/annees-academiques")
@CrossOrigin(origins = "http://localhost:4200")
public class AnneeAcademiqueController {

    private static final Logger logger = LoggerFactory.getLogger(AnneeAcademiqueController.class);

    @Autowired
    private AnneeAcademiqueRepository anneeAcademiqueRepository;

    // Récupérer toutes les années académiques
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllAnneesAcademiques(Authentication authentication) {
        try {
            // Log l'utilisateur qui fait la requête
            logger.info("Utilisateur {} récupère la liste des années académiques", authentication.getName());
            
            List<AnneeAcademique> annees = anneeAcademiqueRepository.findAll();
            
            logger.info("Nombre d'années académiques récupérées : {}", annees.size());
            
            return ResponseEntity.ok(annees);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des années académiques", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Impossible de récupérer les années académiques"));
        }
    }

    // Récupérer une année académique par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAnneeAcademiqueById(@PathVariable Long id) {
        try {
            return anneeAcademiqueRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'année académique avec l'ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du chargement de l'année académique : " + e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createAnneeAcademique(@RequestBody AnneeAcademique anneeAcademique, Authentication authentication) {
        logger.info("Utilisateur {} tente de créer une année académique", authentication.getName());
    
        if (anneeAcademique.getAnnee() == null || anneeAcademique.getAnnee().isBlank()) {
            logger.warn("Tentative de création d'une année académique sans année");
            return ResponseEntity.badRequest().body(Map.of("error", "L'année est obligatoire"));
        }
    
        Optional<AnneeAcademique> existingAnnee = anneeAcademiqueRepository.findByAnnee(anneeAcademique.getAnnee());
        if (existingAnnee.isPresent()) {
            logger.info("Année académique {} existe déjà", anneeAcademique.getAnnee());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Cette année académique existe déjà"));
        }
    
        try {
            String[] annees = anneeAcademique.getAnnee().split("-");
            if (annees.length != 2) {
                logger.warn("Format de libellé incorrect : {}", anneeAcademique.getAnnee());
                return ResponseEntity.badRequest().body(Map.of("error", "Format de libellé incorrect, attendu 'YYYY-YYYY'"));
            }
    
            anneeAcademique.setDateDebut(LocalDate.of(Integer.parseInt(annees[0]), 9, 1));
            anneeAcademique.setDateFin(LocalDate.of(Integer.parseInt(annees[1]), 8, 31));
    
            Optional<AnneeAcademique> savedAnnee = Optional.of(anneeAcademiqueRepository.save(anneeAcademique));
            logger.info("Année académique {} créée avec succès", savedAnnee.get().getAnnee());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAnnee.get());
        } catch (NumberFormatException e) {
            logger.warn("Erreur de conversion de date pour l'année : {}", anneeAcademique.getAnnee());
            return ResponseEntity.badRequest().body(Map.of("error", "Les années doivent être des nombres valides"));
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'année académique", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne lors de la création de l'année académique"));
        }
    }
    
}