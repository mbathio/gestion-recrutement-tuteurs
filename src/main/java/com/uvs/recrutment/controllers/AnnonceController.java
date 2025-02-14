package com.uvs.recrutment.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import com.uvs.recrutment.models.Annonce;
import com.uvs.recrutment.repositories.AnnonceRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/annonces")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class AnnonceController {

    @Autowired
    private AnnonceRepository annonceRepository;

    // Récupérer toutes les annonces
    @GetMapping
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = annonceRepository.findAll();
        return ResponseEntity.ok(annonces);
    }

    // Récupérer une annonce par ID
    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        return annonceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce non trouvée"));
    }

    // Créer une annonce (seul un ADMIN peut le faire)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Annonce> createAnnonce(@RequestBody @Valid Annonce annonce, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données invalides");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(annonceRepository.save(annonce));
    }

    // Mettre à jour une annonce (seul un ADMIN peut le faire)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long id, @RequestBody @Valid Annonce annonceDetails, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données invalides");
        }
        return annonceRepository.findById(id)
                .map(annonce -> {
                    annonce.setTitre(annonceDetails.getTitre());
                    annonce.setDescription(annonceDetails.getDescription());
                    annonce.setLocalisation(annonceDetails.getLocalisation());
                    annonce.setTypePoste(annonceDetails.getTypePoste());
                    annonce.setDateDebut(annonceDetails.getDateDebut());
                    annonce.setDateFin(annonceDetails.getDateFin());
                    annonce.setNiveauQualification(annonceDetails.getNiveauQualification());
                    return ResponseEntity.ok(annonceRepository.save(annonce));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce non trouvée"));
    }

    // Supprimer une annonce (seul un ADMIN peut le faire)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        if (!annonceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce non trouvée");
        }
        annonceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Gestion des erreurs de validation
    @ExceptionHandler(org.springframework.validation.BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(org.springframework.validation.BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
