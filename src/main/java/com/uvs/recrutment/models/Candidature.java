package com.uvs.recrutment.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation avec Candidat (User étendu)
    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;  // Assurez-vous que Candidat est bien défini

    // Relation avec Annonce
    @ManyToOne
    @JoinColumn(name = "annonce_id")
    private Annonce annonce;  // Assurez-vous que Annonce est bien défini

    // Statut de la candidature
    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.EN_COURS;  // Valeur par défaut : EN_COURS

    public enum Statut {
        EN_COURS, ACCEPTEE, REFUSEE
    }
}