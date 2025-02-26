package com.uvs.recrutment.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnneeAcademique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annees", unique = true) // Utilisez le nom de colonne de la base de données
    private String annee; // Champ pour stocker "2023-2024"

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @OneToMany(mappedBy = "anneeAcademique")
    private List<Annonce> annonces;

    // Getter for annee
    public String getAnnee() {
        return annee;
    }

    // Setter for annee
    public void setAnnee(String annee) {
        this.annee = annee;
    }
}
