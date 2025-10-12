package com.basededatosrecetas.recetas.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosrecetas.recetas.Model.Usuario;
import com.basededatosrecetas.recetas.Seguridad.JwtUtil;
import com.basededatosrecetas.recetas.Service.UsuarioService;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
        String token = jwtUtil.generateToken(nuevoUsuario.getEmail(), nuevoUsuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        return usuarioService.updateUsuario(id, usuarioDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        boolean deleted = usuarioService.deleteUsuario(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Usuario loginData) {
        Optional<Usuario> userOpt = usuarioService.getUsuarioByEmail(loginData.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new AuthResponse("Usuario no encontrado"));
        }

        Usuario user = userOpt.get();
        boolean passOk = usuarioService.checkPassword(loginData.getPassword(), user.getPassword());
        if (!passOk) {
            return ResponseEntity.status(401).body(new AuthResponse("Contraseña incorrecta"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRol());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // Clase interna para respuesta de login/registro
    @Data
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
    }
}
