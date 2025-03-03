package com.uvs.recrutment.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private Candidat candidat; // Le candidat qui soumet la candidature

    @ManyToOne
    @JoinColumn(name = "annonce_id", nullable = false)
    @NotNull
    private Annonce annonce; // L'annonce pour laquelle la candidature est soumise

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatutCandidature statut; // Statut de la candidature

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date dateSoumission; // Date de soumission

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereModification; // Date de la dernière mise à jour du statut

    @NotNull
    @Size(min = 2, max = 100)
    private String diplome; // Diplôme le plus élevé

    private int experience; // Expérience en enseignement (en années)

    private String cvPath; // Chemin du fichier CV (PDF)
    private String lettreMotivationPath; // Chemin du fichier Lettre de Motivation (PDF)
    private String justificatifsDiplomesPath; // Chemin du fichier Justificatifs de Diplômes (PDF)

    @Column(columnDefinition = "TEXT")
    private String message; // Message facultatif du candidat

    @Column(columnDefinition = "TEXT")
    private String motifRefus; // Motif du refus (si refusé)

    // Enum pour gérer les statuts de la candidature
    public enum StatutCandidature {
        EN_COURS, EN_ATTENTE, SOUS_EXAMEN, ACCEPTE, REFUSE
    }

    // Mise à jour du statut et de la date de modification
    public void setStatut(StatutCandidature newStatut) {
        this.statut = newStatut;
        this.dateDerniereModification = new Date();
    }

    // Définir le motif de refus uniquement si la candidature est refusée
    public void setMotifRefus(String motif) {
        if (this.statut == StatutCandidature.REFUSE) {
            this.motifRefus = motif;
        } else {
            this.motifRefus = null; // On nettoie si ce n'est pas refusé
        }
    }
}
