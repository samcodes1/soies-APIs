package com.rtechnologies.soies.config;

import com.rtechnologies.soies.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // System.out.println("On the filter level");
            // String token = jwtConfig.extractJwtFromRequest(request);
            // System.out.println("The token is: " + token);
            // String username = jwtConfig.getUsernameFromToken(token);
            // UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            // System.out.println("The User Details is: " + userDetails.getUsername());
            // if (token != null && jwtConfig.validateToken(token, userDetails)) {
            //     System.out.println("Inside if");
            //     // Extract roles from the token
            //     List<String> roles = jwtConfig.getRolesFromToken(token);

            //     System.out.println("Roles" + roles.toString());
            //     // Set up authentication
            //     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            //             username, null, AuthorityUtils.createAuthorityList(roles.toArray(new String[0])));
            //     authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //     SecurityContextHolder.getContext().setAuthentication(authentication);
            // }

        } catch (Exception e) {
            // Handle exception, e.g., log or send a response with an error message
            System.out.println("Error on filter: " + e.toString());
        }

        filterChain.doFilter(request, response);
    }
}
