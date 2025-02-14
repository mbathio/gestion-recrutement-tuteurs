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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) 
                                    throws ServletException, IOException {
        logger.debug("JWT Filter triggered for request: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        // Vérification de l'existence et du format de l'en-tête Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header or incorrect format.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid or missing token");
            return;
        }

        final String token = authHeader.substring(7);
        logger.debug("Extracted Token: " + token);

        // Extraction de l'email et du rôle à partir du jeton
        final String email = jwtUtil.extractUsername(token);
        final String role = jwtUtil.extractRole(token);

        // Vérification de l'extraction de l'email
        if (email == null) {
            logger.warn("Failed to extract username from token.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid token");
            return;
        }

        logger.debug("Extracted Email: " + email + ", Role: " + role);

        // Si l'authentification n'est pas encore définie dans le contexte de sécurité
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = User.builder()
                    .username(email)
                    .password("") // Le mot de passe n'est pas utilisé ici
                    .roles(role)
                    .build();

            // Validation du jeton
            if (jwtUtil.validateToken(token, email)) {
                logger.debug("Token is valid. Setting authentication in SecurityContext.");

                // Mise à jour du contexte de sécurité avec l'authentification
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Token validation failed.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token validation failed");
                return;
            }
        } else {
            logger.debug("SecurityContext already contains authentication.");
        }

        // Passe la requête au filtre suivant
        filterChain.doFilter(request, response);
    }
}
