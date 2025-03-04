package com.uvs.recrutment.services;

import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.repositories.AnneeAcademiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnneeAcademiqueService {

    @Autowired
    private AnneeAcademiqueRepository repository;

    public List<AnneeAcademique> getAllAnnees() {
        return repository.findAll();
    }

    public Optional<AnneeAcademique> getAnneeById(Long id) {
        return repository.findById(id);
    }

    public AnneeAcademique createAnnee(AnneeAcademique annee) {
        if (repository.existsByLibelle(annee.getLibelle())) {
            throw new RuntimeException("Une année académique avec ce libelle existe déjà !");
        }
        return repository.save(annee);
    }

    public AnneeAcademique updateAnnee(Long id, AnneeAcademique updatedAnnee) {
        return repository.findById(id).map(annee -> {
            annee.setLibelle(updatedAnnee.getLibelle());
            annee.setDateDebut(updatedAnnee.getDateDebut());
            annee.setDateFin(updatedAnnee.getDateFin());
            return repository.save(annee);
        }).orElseThrow(() -> new RuntimeException("Année académique non trouvée !"));
    }

    public void deleteAnnee(Long id) {
        repository.deleteById(id);
    }
}
