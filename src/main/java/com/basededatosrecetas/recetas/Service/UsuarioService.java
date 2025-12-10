package com.basededatosrecetas.recetas.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Usuario;
import com.basededatosrecetas.recetas.Repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario createUsuario(Usuario usuario) {
        logger.info("🆕 Creando usuario: email={}", usuario.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ROLE_USER");
        }
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        logger.info("✅ Usuario creado correctamente: id={}, email={}", nuevoUsuario.getId(), nuevoUsuario.getEmail());
        return nuevoUsuario;
    }

    public List<Usuario> getAllUsuarios() {
        logger.info("📋 Obteniendo todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        logger.info("✅ Se encontraron {} usuarios", usuarios.size());
        return usuarios;
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        logger.info("🔍 Buscando usuario por ID: {}", id);
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByEmail(String email) {
        logger.info("🔍 Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> updateUsuario(Long id, Usuario usuarioDetails) {
        logger.info("✏️ Actualizando usuario id={}", id);
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(usuarioDetails.getNombre());
            usuario.setEmail(usuarioDetails.getEmail());
            usuario.setRol(usuarioDetails.getRol() != null ? usuarioDetails.getRol() : usuario.getRol());

            if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
            }

            Usuario actualizado = usuarioRepository.save(usuario);
            logger.info("✅ Usuario actualizado correctamente: id={}, email={}", actualizado.getId(), actualizado.getEmail());
            return actualizado;
        });
    }

    public boolean deleteUsuario(Long id) {
        logger.info("🗑️ Eliminando usuario id={}", id);
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            logger.info("✅ Usuario eliminado correctamente");
            return true;
        }
        logger.warn("⚠️ Usuario con id={} no existe", id);
        return false;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        boolean match = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info(match ? "🔒 Contraseña correcta" : "❌ Contraseña incorrecta");
        return match;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        logger.info("🔑 Cargando usuario para autenticación: email={}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("❌ Usuario no encontrado: email={}", email);
                    return new RuntimeException("Usuario no encontrado");
                });
        logger.info("✅ Usuario cargado correctamente: email={}", email);
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().replace("ROLE_", ""))
                .build();
    }
}
