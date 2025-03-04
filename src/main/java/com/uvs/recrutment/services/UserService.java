package com.uvs.recrutment.services;

import com.uvs.recrutment.models.User;
import com.uvs.recrutment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Récupère tous les utilisateurs (admins et candidats)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Supprime un utilisateur par son ID
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
