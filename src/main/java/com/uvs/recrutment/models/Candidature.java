package com.uvs.recrutment.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "candidature")
public class Candidature {

    public enum StatutCandidature {
        ACCEPTE, EN_COURS, REFUSE;

        public static StatutCandidature getStatutFromString(String statutStr) {
            if (statutStr == null) {
                throw new IllegalArgumentException("Statut cannot be null");
            }
            
            // Normalize the input
            statutStr = statutStr.trim().toUpperCase();
            
            for (StatutCandidature statut : StatutCandidature.values()) {
                if (statut.name().equals(statutStr)) {
                    return statut;
                }
            }
            
            throw new IllegalArgumentException("Statut invalide : " + statutStr);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

    @ManyToOne(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce;

    @Enumerated(EnumType.STRING)
    private StatutCandidature statut;

    private String motifRefus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSoumission;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereModification;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Annonce getAnnonce() {
        return annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public String getMotifRefus() {
        return motifRefus;
    }

    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }

    public Date getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(Date dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public Date getDateDerniereModification() {
        return dateDerniereModification;
    }

    public void setDateDerniereModification(Date dateDerniereModification) {
        this.dateDerniereModification = dateDerniereModification;
    }
}
