package com.task.socialnetwork.config;

import com.task.socialnetwork.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  /**
   * Constructor to inject dependencies.
   *
   * @param jwtService         Service for handling JWT operations.
   * @param userDetailsService Service for loading user details.
   */
  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Filters incoming requests to validate JWT tokens.
   *
   * @param request     The HTTP request.
   * @param response    The HTTP response.
   * @param filterChain The filter chain to process the request.
   * @throws ServletException if any filter processing fails.
   * @throws IOException      if an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String servletPath = request.getServletPath();

    // Skip Swagger endpoints
    if (servletPath.startsWith("/v3/api-docs") || servletPath.startsWith("/swagger-ui")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Extract the "Authorization" header from the request
    final String authHeader = request.getHeader("Authorization");

    // If the Authorization header is missing or doesn't start with "Bearer ", skip processing
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response); // Proceed to the next filter in the chain
      return; // Exit this method early as no further processing is required
    }

    // Extract the token from the Authorization header (removing the "Bearer " prefix)
    final String token = authHeader.substring(7);

    // Use the JwtService to extract the username from the token
    final String username = jwtService.extractUsername(token);

    // Check if the username is valid and there's no existing authentication in the SecurityContext
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // Load user details from the database or another source
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // Validate the token against the loaded user details
      if (jwtService.validateToken(token, userDetails)) {
        // Create an authentication token with the user's details and granted authorities
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Add additional details about the request (e.g., IP, session ID)
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication token in the SecurityContext to authenticate the user
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }

    // Proceed with the rest of the filter chain
    filterChain.doFilter(request, response);
  }
}