package com.basededatosrecetas.recetas.Seguridad;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Permitir OPTIONS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ==================== ACTUATOR ====================
                .requestMatchers("/actuator/**").permitAll()

                // ==================== ENDPOINTS PÚBLICOS ====================
                .requestMatchers(
                    "/api/usuarios/login", 
                    "/api/usuarios/register",
                    "/api/recetas/**",
                    "/api/archivos/**",
                    "/error"
                ).permitAll()

                // ==================== ENDPOINTS DE USUARIO ====================
                .requestMatchers(
                    "/api/subida/**",
                    "/api/recetas/like/**",
                    "/api/recetas/favorito/**",
                    "/api/comentarios/**",
                    "/api/favoritos/**",
                    "/api/historial/**",
                    "/api/perfil/**"
                ).hasAnyRole("USER", "ADMIN")

                // ==================== ENDPOINTS ADMIN ====================
                .requestMatchers(
                    "/api/usuarios/**",
                    "/api/admin/**",
                    "/api/etiquetas/**",
                    "/api/categorias/**"
                ).hasRole("ADMIN")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir credenciales (cookies, tokens, headers Authorization)
        configuration.setAllowCredentials(true);

        // Orígenes permitidos (añade todos los que necesites)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8100",   // Ionic dev server
            "https://localhost"        // Capacitor / HTTPS
            // Puedes agregar más dominios de producción aquí
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
