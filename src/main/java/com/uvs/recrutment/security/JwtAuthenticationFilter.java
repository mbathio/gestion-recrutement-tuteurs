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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    
    // List of public endpoints that don't require authentication
    private final List<String> publicEndpoints = Arrays.asList(
        "/api/auth/register",
        "/api/auth/login"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return publicEndpoints.stream().anyMatch(endpoint -> path.startsWith(endpoint));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) 
                                    throws ServletException, IOException {
        logger.debug("JWT Filter triggered for request: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        // If no Authorization header or incorrect format, continue to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header or incorrect format.");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        logger.debug("Extracted Token: " + token);

        try {
            // Extract email and role from token
            final String email = jwtUtil.extractUsername(token);
            final String role = jwtUtil.extractRole(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(token)) {
                    UserDetails userDetails = User.withUsername(email)
                            .password("") // Not needed for token authentication
                            .authorities(role)
                            .build();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication successful for user: " + email);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
