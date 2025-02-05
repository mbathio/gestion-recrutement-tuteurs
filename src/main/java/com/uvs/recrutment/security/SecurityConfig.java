package com.uvs.recrutment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Autoriser l'accès aux pages de login et register sans authentification
                        .requestMatchers("/auth/login", "/auth/register").permitAll() // Permettre l'accès sans authentification
                        .requestMatchers(HttpMethod.GET, "/").permitAll() // Autoriser la page d'accueil sans authentification
                        .requestMatchers("/annonces/**").permitAll() // Permettre l'accès public aux annonces
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")  // Protège les routes admin
                        .requestMatchers("/candidat/**").hasAuthority("CANDIDAT") // Protège les routes candidat
                        .requestMatchers("/candidatures/**").hasAuthority("CANDIDAT") // Protège les candidatures
                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuration pour l'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
