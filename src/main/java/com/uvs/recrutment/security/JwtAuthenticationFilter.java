package com.uvs.recrutment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    // Définition des points d'entrée publics (qui ne nécessitent pas d'authentification)
    private final List<String> publicEndpoints = Arrays.asList(
        "/",
        "/error",
        "/api/auth/register",
        "/api/auth/login"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return publicEndpoints.stream().anyMatch(endpoint ->
            request.getRequestURI().startsWith(endpoint)
        );
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {
        logger.debug("JWT Filter triggered for request: {}", request.getRequestURI());

        // Récupérer le header Authorization
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header or incorrect format.");
            filterChain.doFilter(request, response); // Laisse passer la requête si l'entête n'est pas valide
            return;
        }

        final String token = authHeader.substring(7);  // Extraire le token
        logger.debug("Extracted Token: {}", token);

        try {
            // Extraire les informations du token
            final String email = jwtUtil.extractUsername(token);
            final String role = jwtUtil.extractRole(token); // Assurez-vous que le rôle est extrait correctement

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Assurez-vous que le rôle contient le préfixe ROLE_ (par ex : ROLE_ADMIN)
                UserDetails userDetails = User.withUsername(email)
                        .password("") // Pas de mot de passe nécessaire ici, car géré par JWT
                        .authorities("ROLE_" + role) // Associer les rôles du token, en ajoutant le préfixe ROLE_
                        .build();

                // Validation du token
                if (jwtUtil.validateToken(token, email)) {
                    // Si le token est valide, créer une authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Authentifier l'utilisateur
                    logger.debug("Authentication successful for user: {}", email);
                } else {
                    // Si le token est invalide ou expiré, renvoyer une erreur
                    logger.warn("Invalid or expired JWT token.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.getWriter().write("Invalid or expired token");
                    return;
                }
            }
        } catch (Exception e) {
            // Gestion des erreurs lors du traitement du JWT
            logger.error("Error processing JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Invalid token");
            return;
        }

        // Passer la requête au prochain filtre de la chaîne
        filterChain.doFilter(request, response);
    }
}
