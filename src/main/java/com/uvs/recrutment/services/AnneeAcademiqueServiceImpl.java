package com.uvs.recrutment.services;

import com.uvs.recrutment.models.AnneeAcademique;
import com.uvs.recrutment.repositories.AnneeAcademiqueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AnneeAcademiqueServiceImpl implements AnneeAcademiqueService {

    private static final Logger logger = LoggerFactory.getLogger(AnneeAcademiqueServiceImpl.class);
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    public AnneeAcademiqueServiceImpl(AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnneeAcademique> getAllAnneesAcademiques() {
        try {
            logger.info("Attempting to retrieve all academic years");
            List<AnneeAcademique> annees = anneeAcademiqueRepository.findAll();
            
            if (annees.isEmpty()) {
                logger.warn("No academic years found in the database");
            }
            
            logger.info("Retrieved {} academic years", annees.size());
            return annees;
        } catch (DataAccessException e) {
            logger.error("Database access error while retrieving academic years", e);
            throw new RuntimeException("Database error: Unable to retrieve academic years", e);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving academic years", e);
            throw new RuntimeException("Unexpected error: Unable to retrieve academic years", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnneeAcademique> getAnneeAcademiqueById(Long id) {
        try {
            logger.info("Attempting to retrieve academic year by ID: {}", id);
            Optional<AnneeAcademique> annee = anneeAcademiqueRepository.findById(id);
            
            if (!annee.isPresent()) {
                logger.warn("Academic year not found with ID: {}", id);
            }
            
            logger.info("Retrieved academic year by ID: {}", id);
            return annee;
        } catch (DataAccessException e) {
            logger.error("Database access error while retrieving academic year by ID", e);
            throw new RuntimeException("Database error: Unable to retrieve academic year by ID", e);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving academic year by ID", e);
            throw new RuntimeException("Unexpected error: Unable to retrieve academic year by ID", e);
        }
    }

    @Override
    @Transactional
    public AnneeAcademique createAnneeAcademique(AnneeAcademique anneeAcademique) {
        try {
            logger.info("Attempting to create new academic year");
            if (anneeAcademique.getAnnee() == null || anneeAcademique.getAnnee().isBlank()) {
                throw new IllegalArgumentException("L'année est obligatoire");
            }

            Optional<AnneeAcademique> existingAnnee = anneeAcademiqueRepository.findByAnnee(anneeAcademique.getAnnee());
            if (existingAnnee.isPresent()) {
                throw new IllegalStateException("Cette année académique existe déjà");
            }

            String[] annees = anneeAcademique.getAnnee().split("-");
            if (annees.length != 2) {
                throw new IllegalArgumentException("Format de libellé incorrect, attendu 'YYYY-YYYY'");
            }

            anneeAcademique.setDateDebut(LocalDate.of(Integer.parseInt(annees[0]), 9, 1));
            anneeAcademique.setDateFin(LocalDate.of(Integer.parseInt(annees[1]), 8, 31));

            logger.info("Saving new academic year");
            AnneeAcademique savedAnnee = anneeAcademiqueRepository.save(anneeAcademique);
            logger.info("New academic year saved successfully");
            return savedAnnee;
        } catch (NumberFormatException e) {
            logger.error("Invalid year format", e);
            throw new IllegalArgumentException("Les années doivent être des nombres valides");
        } catch (DataAccessException e) {
            logger.error("Database access error while creating new academic year", e);
            throw new RuntimeException("Database error: Unable to create new academic year", e);
        } catch (Exception e) {
            logger.error("Unexpected error while creating new academic year", e);
            throw new RuntimeException("Unexpected error: Unable to create new academic year", e);
        }
    }
}
