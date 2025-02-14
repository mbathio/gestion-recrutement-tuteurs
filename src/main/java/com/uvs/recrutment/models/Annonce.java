package com.uvs.recrutment.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private AnneeAcademique anneeAcademique;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL)
    private List<Candidature> candidatures;

    @NotBlank(message = "La localisation est obligatoire")
    private String localisation;

    @NotBlank(message = "Le type de poste est obligatoire")
    private String typePoste;

    @NotBlank(message = "Le niveau de qualification est obligatoire")
    private String niveauQualification;

    @Column(columnDefinition = "TEXT")
    private String detailsPoste;

    private String conditionDepot;
    private String lienDepot;

    @NotNull(message = "La date de début est obligatoire")
    private String dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private String dateFin;
}
