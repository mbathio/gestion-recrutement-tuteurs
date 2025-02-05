package com.uvs.recrutment.controllers;

import com.uvs.recrutment.models.Candidature;
import com.uvs.recrutment.models.User;
import com.uvs.recrutment.repositories.CandidatureRepository;
import com.uvs.recrutment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/candidatures")
public class CandidatureController {

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private UserRepository userRepository;

    //  Un candidat peut voir ses candidatures
    @GetMapping("/{email}")
    @PreAuthorize("hasAuthority('CANDIDAT')")
    public List<Candidature> getCandidaturesByEmail(@PathVariable String email) {
        Optional<User> candidat = userRepository.findByEmail(email);
        return candidat.map(user -> candidatureRepository.findByCandidat(user)).orElse(null);
    }

    //  Un candidat peut soumettre une candidature
    @PostMapping
    @PreAuthorize("hasAuthority('CANDIDAT')")
    public Candidature postuler(@RequestBody Candidature candidature) {
        return candidatureRepository.save(candidature);
    }

    //  Un administrateur peut valider ou refuser une candidature
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Candidature traiterCandidature(@PathVariable Long id, @RequestParam String statut) {
        Optional<Candidature> candidatureOpt = candidatureRepository.findById(id);
        if (candidatureOpt.isPresent()) {
            Candidature candidature = candidatureOpt.get();
            candidature.setStatut(Candidature.Statut.valueOf(statut));
            return candidatureRepository.save(candidature);
        }
        return null;
    }
}