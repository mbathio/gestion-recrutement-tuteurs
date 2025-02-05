package com.uvs.recrutment.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    private String description;

    @ManyToOne
    private AnneeAcademique anneeAcademique;

    @OneToMany(mappedBy = "annonce")
    private List<Candidature> candidatures;
}