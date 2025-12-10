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
        logger.info("Creando usuario {}", usuario.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> updateUsuario(Long id, Usuario usuarioDetails) {
        return usuarioRepository.findById(id).map(usuario -> {

            // EDITAR CAMPOS DEL PERFIL
            if (usuarioDetails.getNombre() != null)
                usuario.setNombre(usuarioDetails.getNombre());

            if (usuarioDetails.getDescripcion() != null)
                usuario.setDescripcion(usuarioDetails.getDescripcion());

            if (usuarioDetails.getImagenPerfil() != null)
                usuario.setImagenPerfil(usuarioDetails.getImagenPerfil());

            if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
            }

            return usuarioRepository.save(usuario);
        });
    }

    public boolean deleteUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}
}
