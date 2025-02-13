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
    private StatutCandidature statut; // Le statut de la candidature (EN_COURS, ACCEPTE, REFUSE)

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSoumission; // Date de soumission de la candidature

    private String diplome; // Diplôme le plus élevé

    private int experience; // Expérience en enseignement (en années)

    private String cvPath; // Chemin du fichier CV (PDF)
    private String lettreMotivationPath; // Chemin du fichier Lettre de Motivation (PDF)
    private String justificatifsDiplomesPath; // Chemin du fichier Justificatifs de Diplômes (PDF)

    @Column(columnDefinition = "TEXT")
    private String message; // Message facultatif

    // Enum pour gérer les statuts de la candidature
    public enum StatutCandidature {
        EN_COURS, ACCEPTE, REFUSE
    }
}
