package com.uvs.recrutment.models;

public class Status {
    private String statut;
    private String motifRefus;

    // Constructeurs
    public Status(String statut, String motifRefus) {
        this.statut = statut;
        this.motifRefus = motifRefus;
    }

    // Getters et setters
    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotifRefus() {
        return motifRefus;
    }

    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }
}
