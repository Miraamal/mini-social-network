package com.task.socialnetwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Configures the AuthenticationManager using Spring's AuthenticationConfiguration.
   *
   * @param configuration The authentication configuration.
   * @return The AuthenticationManager instance.
   * @throws Exception if configuration fails.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  /**
   * Defines the security filter chain and HTTP security configurations.
   *
   * @param http The HTTP security configuration.
   * @return The configured SecurityFilterChain.
   * @throws Exception if configuration fails.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless authentication.
        .authorizeHttpRequests(auth -> auth
            // Allow access to Swagger/OpenAPI documentation
            // Publicly accessible endpoints (e.g., Swagger documentation and authentication)
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/**"
            ).permitAll()
            .requestMatchers("/api/auth/**").permitAll() // Register and Login

            // All other requests require authentication
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS)) // Stateless session management.
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class)// Add the JWT filter before UsernamePasswordAuthenticationFilter.
        .exceptionHandling(exception -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpStatus.FORBIDDEN.value());
              response.getWriter().write("Access Denied: " + accessDeniedException.getMessage());
            })
        );

    return http.build();
  }

  /**
   * Configures a password encoder bean.
   *
   * @return An instance of BCryptPasswordEncoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}