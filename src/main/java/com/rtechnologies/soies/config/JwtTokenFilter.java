package com.rtechnologies.soies.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;

    public JwtTokenFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = jwtConfig.extractJwtFromRequest(request);

            if (token != null && jwtConfig.validateToken(token)) {
                String username = jwtConfig.getUsernameFromToken(token);

                // Extract roles from the token
                Set<String> roles = jwtConfig.getRolesFromToken(token);

                // Set up authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, AuthorityUtils.createAuthorityList(roles.toArray(new String[0])));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            // Handle exception, e.g., log or send a response with an error message
        }

        filterChain.doFilter(request, response);
    }
}
