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

    @OneToOne(mappedBy = "candidat", cascade = CascadeType.ALL)
    private Candidature candidature;
}