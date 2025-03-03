package com.uvs.recrutment.services;

import com.uvs.recrutment.models.AnneeAcademique;
import java.util.List;
import java.util.Optional;

public interface AnneeAcademiqueService {
    List<AnneeAcademique> getAllAnneesAcademiques();
    Optional<AnneeAcademique> getAnneeAcademiqueById(Long id);
    AnneeAcademique createAnneeAcademique(AnneeAcademique anneeAcademique);
}
