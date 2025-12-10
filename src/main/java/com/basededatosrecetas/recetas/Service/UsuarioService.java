package com.basededatosrecetas.recetas.Service;

import com.basededatosrecetas.recetas.Model.Usuario;
import com.basededatosrecetas.recetas.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Obtener todos los usuarios
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener usuario por ID
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Obtener usuario por email
    public Optional<Usuario> getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Crear nuevo usuario
    public Usuario createUsuario(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Establecer valores por defecto
        usuario.setRol(usuario.getRol() != null ? usuario.getRol() : "USER");
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());
        
        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario (para uso del administrador)
    public Optional<Usuario> updateUsuario(Long id, Usuario usuarioDetails) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Solo actualizar campos permitidos
                    if (usuarioDetails.getNombre() != null) {
                        usuario.setNombre(usuarioDetails.getNombre());
                    }
                    if (usuarioDetails.getDescripcion() != null) {
                        usuario.setDescripcion(usuarioDetails.getDescripcion());
                    }
                    if (usuarioDetails.getFotoBase64() != null) {
                        usuario.setFotoBase64(usuarioDetails.getFotoBase64());
                        usuario.setFotoTipo(usuarioDetails.getFotoTipo());
                    }
                    if (usuarioDetails.getRol() != null) {
                        usuario.setRol(usuarioDetails.getRol());
                    }
                    
                    // Actualizar fecha de modificación
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    
                    return usuarioRepository.save(usuario);
                });
    }

    // Actualizar perfil del usuario actual (para uso del usuario)
    public Optional<Usuario> updatePerfil(Long id, Usuario usuarioDetails) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Solo permitir actualizar ciertos campos en el perfil
                    if (usuarioDetails.getNombre() != null) {
                        usuario.setNombre(usuarioDetails.getNombre());
                    }
                    
                    if (usuarioDetails.getDescripcion() != null) {
                        usuario.setDescripcion(usuarioDetails.getDescripcion());
                    }
                    
                    if (usuarioDetails.getFotoBase64() != null) {
                        usuario.setFotoBase64(usuarioDetails.getFotoBase64());
                        usuario.setFotoTipo(usuarioDetails.getFotoTipo());
                    }
                    
                    // No permitir cambiar email o rol desde el perfil
                    
                    // Actualizar fecha de modificación
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    
                    return usuarioRepository.save(usuario);
                });
    }

    // Eliminar foto de perfil
    public Optional<Usuario> eliminarFotoPerfil(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setFotoBase64(null);
                    usuario.setFotoTipo(null);
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    return usuarioRepository.save(usuario);
                });
    }

    // Eliminar usuario
    public boolean deleteUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar contraseña
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Cambiar contraseña
    public Optional<Usuario> changePassword(Long id, String currentPassword, String newPassword) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Verificar contraseña actual
                    if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
                        throw new RuntimeException("Contraseña actual incorrecta");
                    }
                    
                    // Validar nueva contraseña
                    if (newPassword.length() < 6) {
                        throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
                    }
                    
                    // Encriptar nueva contraseña
                    usuario.setPassword(passwordEncoder.encode(newPassword));
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    
                    return usuarioRepository.save(usuario);
                });
    }

    // Verificar si email existe
    public boolean emailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}