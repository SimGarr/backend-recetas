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
                    "/api/perfil/**",  // ← AGREGADO AQUÍ
                    "/like/**",
                    "/historial/**"
                ).hasAnyRole("USER", "ADMIN")

                // ==================== ENDPOINTS ADMIN ====================
                .requestMatchers(
                    "/api/usuarios/**",
                    "/api/admin/**",
                    "/api/etiquetas/**",
                    "/api/categorias/**"
                ).hasRole("ADMIN")

                // ==================== ENDPOINTS PÚBLICOS DE LECTURA ====================
                .requestMatchers(HttpMethod.GET, "/comentarios/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/etiquetas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/like/receta/**").permitAll()

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
        
        // AÑADIDO: "*" para desarrollo, pero mejor usar patrones específicos
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "*",                           // Permite todos en desarrollo (quitar en producción)
            "http://localhost:*",          // Cualquier puerto localhost
            "https://localhost:*",         // Cualquier puerto HTTPS localhost  
            "http://localhost:8100",       // Ionic serve
            "https://localhost:8100",      // Ionic serve con HTTPS
            "https://localhost",           // Capacitor Android/Web
            "capacitor://localhost",       // Capacitor nativo (Android/iOS)
            "ionic://localhost",           // Ionic nativo
            "http://localhost",            // Desarrollo local
            "https://apprecetas.serveblog.net", // Tu dominio
            "https://apprecetas.duckdns.org" // Tu dominio alternativo
        ));
        
        configuration.setAllowCredentials(true);
        
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Headers permitidos
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
        
        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Content-Disposition",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Tiempo máximo de caché para preflight (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}