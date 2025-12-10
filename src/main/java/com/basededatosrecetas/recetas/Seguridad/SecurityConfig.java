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
                // **CRÍTICO: Permitir OPTIONS PRIMERO**
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ==================== ACTUATOR ====================
                .requestMatchers("/actuator/**").permitAll()

                // ==================== ENDPOINTS PÚBLICOS ====================
                // **ESPECÍFICOS primero**
                .requestMatchers("/api/usuarios/login").permitAll()
                .requestMatchers("/api/usuarios/register").permitAll()
                .requestMatchers("/error").permitAll()
                
                // **Lectura pública de recetas**
                .requestMatchers(HttpMethod.GET, "/api/recetas/**").permitAll()
                
                // **Lectura pública de comentarios**
                .requestMatchers(HttpMethod.GET, "/comentarios/**").permitAll()
                
                // **Lectura pública de etiquetas**
                .requestMatchers(HttpMethod.GET, "/etiquetas/**").permitAll()

                // ==================== ENDPOINTS DE USUARIO ====================
                .requestMatchers(
                    "/api/subida/**",
                    "/api/recetas/like/**",
                    "/api/recetas/favorito/**",
                    "/api/comentarios/**",
                    "/api/favoritos/**",
                    "/api/historial/**",
                    "/api/perfil/**"      // ← AGREGADO AQUÍ
                ).hasAnyRole("USER", "ADMIN")

                // **Escritura de recetas (POST, PUT, DELETE)**
                .requestMatchers(HttpMethod.POST, "/api/recetas/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/recetas/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/recetas/**").hasAnyRole("USER", "ADMIN")

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
        
        // **SOLUCIÓN: Agrega estos orígenes específicos**
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://localhost",          // Esto es lo que usa Capacitor
            "capacitor://localhost",      // Nativo
            "ionic://localhost",          // Ionic nativo
            "http://localhost:8100",      // Ionic serve
            "http://localhost",           // Local sin puerto
            "https://localhost:8100",     // HTTPS local
            "https://apprecetas.serveblog.net"
        ));
        
        configuration.setAllowCredentials(true);
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-CSRF-Token"
        ));
        
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Content-Disposition",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}