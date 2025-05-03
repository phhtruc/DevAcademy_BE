package com.devacademy.DevAcademy_BE.config;

import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.devacademy.DevAcademy_BE.repository.TokenRepository;
import com.devacademy.DevAcademy_BE.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtService jwtService;
    UserDetailsService userDetailsService;
    TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get the Token String
        final String jwtToken = authorizationHeader.substring(7);

        try {
            // Extract the user information from the token
            final String userEmail = jwtService.extractUsername(jwtToken, TokenType.ACCESS_TOKEN);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Check if the token is valid and not revoked
                var isToken = tokenRepository.findByToken(jwtToken)
                        .map(t -> !t.isRevoked() && !t.isExpired()).orElse(false);

                if (jwtService.isTokenValid(jwtToken, userDetails, TokenType.ACCESS_TOKEN) && isToken) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response); // Continue with the filter chain

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token expired - respond with Unauthorized (401)
            log.warn("JWT Token has expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token has expired. Please login again.\"}");
            response.getWriter().flush();

        } catch (Exception e) {
            // Any other authentication errors
            log.error("Authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication failed. Token invalid.\"}");
            response.getWriter().flush();
        }
    }
}
