package com.basededatosrecetas.recetas.Seguridad;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // ==================== ENDPOINTS PÚBLICOS ====================
                .requestMatchers(
                    "/api/usuarios/login", 
                    "/api/usuarios/register",
                    "/api/recetas/**",           // GET de recetas públicos
                    "/api/archivos/**",          // Servir archivos (público para ver recetas)
                    "/error"
                ).permitAll()
                
                // ==================== ENDPOINTS DE SUBIDA (AUTENTICADOS) ====================
                .requestMatchers(
                    "/api/subida/**",            // Subir recetas con archivos
                    "/api/recetas",              // POST crear receta (con autenticación)
                    "/api/recetas/**/like",      // Like a recetas
                    "/api/recetas/**/favorito",  // Favoritos
                    "/api/comentarios/**",       // Comentarios
                    "/api/favoritos/**",         // Gestión de favoritos
                    "/api/historial/**",         // Historial de usuario
                    "/api/perfil/**"             // Perfil de usuario
                ).hasAnyRole("USER", "ADMIN")
                
                // ==================== ENDPOINTS ADMIN ====================
                .requestMatchers(
                    "/api/usuarios/**",          // Gestión de usuarios
                    "/api/admin/**",             // Endpoints administrativos
                    "/api/etiquetas/**",         // Gestión de etiquetas
                    "/api/categorias/**"         // Gestión de categorías
                ).hasRole("ADMIN")
                
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
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Content-Disposition"
        ));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}