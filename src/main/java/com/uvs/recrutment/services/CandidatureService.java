package com.uvs.recrutment.services;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.Candidature.StatutCandidature;
import com.uvs.recrutment.repositories.CandidatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

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
     * Mettre à jour le statut d'une candidature (Validation ou Refus)
     * @param candidatureId ID de la candidature
     * @param statut Nouveau statut (ACCEPTE, REFUSE, EN_COURS, etc.)
     * @param motifRefus Motif du refus (facultatif, uniquement si refusé)
     * @return Candidature mise à jour
     */
    public Candidature updateCandidatureStatus(Long candidatureId, StatutCandidature statut, String motifRefus) {
        Candidature candidature = getCandidatureById(candidatureId);
        
        // Mise à jour du statut
        candidature.setStatut(statut);
        candidature.setDateDerniereModification(new Date());

        // Gestion du motif de refus uniquement si la candidature est refusée
        if (statut == StatutCandidature.REFUSE) {
            if (motifRefus == null || motifRefus.trim().isEmpty()) {
                throw new IllegalArgumentException("Un motif est requis pour un refus de candidature.");
            }
            candidature.setMotifRefus(motifRefus);
        } else {
            candidature.setMotifRefus(null); // On nettoie le champ si le statut n'est pas REFUSE
        }

        return candidatureRepository.save(candidature);
    }
}
