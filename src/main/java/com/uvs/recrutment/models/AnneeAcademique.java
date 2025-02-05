package com.uvs.recrutment.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnneeAcademique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String annee;

    @OneToMany(mappedBy = "anneeAcademique")
    private List<Annonce> annonces;
}
