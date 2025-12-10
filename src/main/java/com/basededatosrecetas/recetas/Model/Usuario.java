package com.basededatosrecetas.recetas.Model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol = "USER"; // USER o ADMIN

    // NUEVOS CAMPOS PARA EL PERFIL
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Lob
    @Column(name = "foto_base64", length = 10485760) // 10MB para foto
    private String fotoBase64;

    @Column(name = "foto_tipo")
    private String fotoTipo;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion = java.time.LocalDateTime.now();

    // Método para obtener la foto completa como data URL
    public String getFotoCompleta() {
        if (fotoBase64 != null && fotoTipo != null) {
            return "data:" + fotoTipo + ";base64," + fotoBase64;
        }
        return null;
    }

    // Implementación de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}