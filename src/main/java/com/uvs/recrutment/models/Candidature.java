package com.uvs.recrutment.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat; // Le candidat qui soumet la candidature

    @ManyToOne
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce; // L'annonce pour laquelle la candidature est soumise

    @Enumerated(EnumType.STRING)
    private StatutCandidature statut; // Le statut de la candidature (EN_COURS, ACCEPTÉ, REFUSÉ)

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSoumission; // Date de soumission de la candidature

    // Enum pour gérer les statuts de la candidature
    public enum StatutCandidature {
        EN_COURS, ACCEPTE, REFUSE
    }
}
