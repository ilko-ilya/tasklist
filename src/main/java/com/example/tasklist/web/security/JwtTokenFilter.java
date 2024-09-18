package com.example.tasklist.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain
    ) throws IOException, ServletException {
        String bearerToken = ((HttpServletRequest) servletRequest)
                .getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
            LOGGER.debug("Extracted Bearer Token: {}", bearerToken);
        }
        if (bearerToken != null && jwtTokenProvider
                .validateToken(bearerToken)) {
            try {
                Authentication authentication = jwtTokenProvider
                        .getAuthentication(bearerToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                    LOGGER.debug(
                            "Authentication set for: {}",
                            authentication.getName()
                    );
                }
            } catch (Exception e) {
                LOGGER.error("Authentication failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

