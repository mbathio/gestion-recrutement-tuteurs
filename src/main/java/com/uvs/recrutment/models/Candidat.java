package com.uvs.recrutment.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Candidat extends User {

    private String photoProfil;
    
    private String telephone; // Ajout du champ téléphone

    public String getTelephone() { // Getter pour le téléphone
        return telephone;
    }

    public void setTelephone(String telephone) { // Setter pour le téléphone
        this.telephone = telephone;
    }

    @OneToOne(mappedBy = "candidat", cascade = CascadeType.ALL)
    private Candidature candidature;
}
