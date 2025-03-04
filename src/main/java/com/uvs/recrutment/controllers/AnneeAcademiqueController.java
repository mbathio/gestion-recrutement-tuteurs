package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.services.AnneeAcademiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/annees-academiques")
public class AnneeAcademiqueController {

    @Autowired
    private AnneeAcademiqueService service;

    @GetMapping
    public List<AnneeAcademique> getAllAnnees() {
        return service.getAllAnnees();
    }

    @GetMapping("/{id}")
    public Optional<AnneeAcademique> getAnneeById(@PathVariable Long id) {
        return service.getAnneeById(id);
    }

    @PostMapping
    public AnneeAcademique createAnnee(@RequestBody AnneeAcademique annee) {
        return service.createAnnee(annee);
    }

    @PutMapping("/{id}")
    public AnneeAcademique updateAnnee(@PathVariable Long id, @RequestBody AnneeAcademique updatedAnnee) {
        return service.updateAnnee(id, updatedAnnee);
    }

    @DeleteMapping("/{id}")
    public void deleteAnnee(@PathVariable Long id) {
        service.deleteAnnee(id);
    }
}
