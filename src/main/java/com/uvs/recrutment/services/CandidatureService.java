package com.uvs.recrutment.services;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.Candidature.StatutCandidature;
import com.uvs.recrutment.repositories.CandidatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CandidatureService {

    @Autowired
    private CandidatureRepository candidatureRepository;

    /**
     * Récupérer une candidature par son ID
     * @param candidatureId ID de la candidature
     * @return Objet Candidature si trouvé, sinon exception
     */
    public Candidature getCandidatureById(Long candidatureId) {
        return candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée avec l'ID : " + candidatureId));
    }

    /**
     * Récupérer une page de candidatures
     * @param pageable Informations de pagination
     * @return Page de candidatures
     */
    public Page<Candidature> getCandidaturesPage(Pageable pageable) {
        return candidatureRepository.findAll(pageable);
    }

     /**
     * Mettre à jour le statut d'une candidature
     * @param candidatureId ID de la candidature
     * @param statut Nouveau statut (ACCEPTE, REFUSE, EN_COURS, etc.)
     * @param motifRefus Motif du refus (facultatif, uniquement si refusé)
     * @return Candidature mise à jour
     */
    public Candidature updateCandidatureStatus(Long candidatureId, StatutCandidature statut, String motifRefus) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
    
        // Mise à jour du statut
        candidature.setStatut(statut);
        candidature.setDateDerniereModification(new Date());
    
        // Si refusé, ajouter le motif
        if (statut == StatutCandidature.REFUSE) {
            if (motifRefus == null || motifRefus.trim().isEmpty()) {
                throw new IllegalArgumentException("Un motif de refus est requis");
            }
            candidature.setMotifRefus(motifRefus);
        } else {
            candidature.setMotifRefus(null);
        }
    
        // Sauvegarder la candidature mise à jour
        return candidatureRepository.save(candidature);
    }
    

   

    /**
     * Supprimer une candidature
     * @param id ID de la candidature à supprimer
     * @return true si la suppression est effectuée, sinon false
     */
    public boolean deleteCandidature(Long id) {
        if (candidatureRepository.existsById(id)) {
            candidatureRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Créer une nouvelle candidature
     * @param candidature Objet Candidature à créer
     * @return La candidature créée
     */
    public Candidature createCandidature(Candidature candidature) {
        // Vous pouvez ajouter ici des validations ou des actions avant de sauvegarder la candidature
        return candidatureRepository.save(candidature);
    }

    /**
     * Récupérer toutes les candidatures
     * @return Liste de toutes les candidatures
     */
    public List<Candidature> getAllCandidatures() {
        return candidatureRepository.findAll();
    }

    

    
}
