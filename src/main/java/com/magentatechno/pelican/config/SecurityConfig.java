package com.magentatechno.pelican.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST stateless avec JWT
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                // Pas de session côté serveur
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Headers de sécurité
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(Customizer.withDefaults())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .referrerPolicy(ref ->
                                ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                        )
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; " +
                                        "object-src 'none'; " +
                                        "frame-ancestors 'none'; " +
                                        "base-uri 'self'"
                                )
                        )
                )

                // Règles d'accès
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/public/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/courriers/**").authenticated()
                        .requestMatchers("/dashboard/**").authenticated()
                        .requestMatchers("/notifications/**").authenticated()
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                // Filtres
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
