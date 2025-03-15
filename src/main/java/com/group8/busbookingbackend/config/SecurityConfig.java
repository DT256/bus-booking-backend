package com.group8.busbookingbackend.config;


import com.group8.busbookingbackend.exception.FilterExceptionHandler;
import com.group8.busbookingbackend.exception.JwtAuthenticationEntryPoint;
import com.group8.busbookingbackend.security.JwtValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{
//    @NonFinal
//    @Value("${cors.allowed.origins}")
//    private String[] allowedOrigins;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/*").authenticated()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().permitAll());

        httpSecurity.sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.addFilterBefore(new JwtValidator(), BasicAuthenticationFilter.class);
        httpSecurity.addFilterBefore(new FilterExceptionHandler(), JwtValidator.class);
        httpSecurity.exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return httpSecurity.build();

    }

    private CorsConfigurationSource corsConfigurationSource()
    {
        // Interface with 1 method
        return request ->
        {
            CorsConfiguration configuration = new CorsConfiguration();
//            configuration.setAllowedOrigins(List.of(allowedOrigins));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setExposedHeaders(List.of("Authorization"));
            configuration.setMaxAge(3600L);
            return configuration;
        };
    }
}

