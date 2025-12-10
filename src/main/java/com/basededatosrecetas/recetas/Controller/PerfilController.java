package com.basededatosrecetas.recetas.Controller;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosrecetas.recetas.Model.Usuario;
import com.basededatosrecetas.recetas.Service.UsuarioService;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener perfil del usuario autenticado
    @GetMapping
    public ResponseEntity<?> getPerfil(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Usuario no encontrado"));
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Crear respuesta con datos necesarios
            Map<String, Object> perfilData = new HashMap<>();
            perfilData.put("id", usuario.getId());
            perfilData.put("nombre", usuario.getNombre());
            perfilData.put("email", usuario.getEmail());
            perfilData.put("rol", usuario.getRol());
            perfilData.put("descripcion", usuario.getDescripcion());
            perfilData.put("fechaCreacion", usuario.getFechaCreacion());
            perfilData.put("fechaActualizacion", usuario.getFechaActualizacion());
            
            // Agregar foto si existe
            if (usuario.getFotoBase64() != null && usuario.getFotoTipo() != null) {
                perfilData.put("fotoBase64", usuario.getFotoBase64());
                perfilData.put("fotoTipo", usuario.getFotoTipo());
                perfilData.put("fotoCompleta", usuario.getFotoCompleta());
            }
            
            // Obtener estadísticas del usuario
            Map<String, Object> estadisticas = usuarioService.getEstadisticasUsuario(usuario.getId());
            perfilData.putAll(estadisticas);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "usuario", perfilData
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al obtener perfil: " + e.getMessage()));
        }
    }

    // Actualizar perfil del usuario
    @PutMapping
    public ResponseEntity<?> updatePerfil(
            @RequestBody Map<String, Object> datos,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Usuario no encontrado"));
            }
            
            Usuario usuario = usuarioOpt.get();
            Usuario usuarioActualizado = new Usuario();
            
            // Actualizar campos permitidos
            if (datos.containsKey("nombre")) {
                usuarioActualizado.setNombre((String) datos.get("nombre"));
            }
            
            if (datos.containsKey("descripcion")) {
                usuarioActualizado.setDescripcion((String) datos.get("descripcion"));
            }
            
            // Actualizar foto (Base64)
            if (datos.containsKey("fotoBase64") && datos.get("fotoBase64") != null) {
                String base64Data = (String) datos.get("fotoBase64");
                // Si viene con prefijo data:image/...;base64, lo limpiamos
                if (base64Data.contains("base64,")) {
                    base64Data = base64Data.split("base64,")[1];
                }
                usuarioActualizado.setFotoBase64(base64Data);
                usuarioActualizado.setFotoTipo((String) datos.get("fotoTipo"));
            } else if (datos.containsKey("eliminarFoto") && Boolean.TRUE.equals(datos.get("eliminarFoto"))) {
                // Eliminar foto si se solicita
                usuarioActualizado.setFotoBase64(null);
                usuarioActualizado.setFotoTipo(null);
            }
            
            Optional<Usuario> resultado = usuarioService.updatePerfil(usuario.getId(), usuarioActualizado);
            
            if (resultado.isPresent()) {
                Usuario updatedUsuario = resultado.get();
                
                // Preparar respuesta
                Map<String, Object> perfilData = new HashMap<>();
                perfilData.put("id", updatedUsuario.getId());
                perfilData.put("nombre", updatedUsuario.getNombre());
                perfilData.put("email", updatedUsuario.getEmail());
                perfilData.put("rol", updatedUsuario.getRol());
                perfilData.put("descripcion", updatedUsuario.getDescripcion());
                perfilData.put("fechaCreacion", updatedUsuario.getFechaCreacion());
                perfilData.put("fechaActualizacion", updatedUsuario.getFechaActualizacion());
                
                // Agregar foto si existe
                if (updatedUsuario.getFotoBase64() != null && updatedUsuario.getFotoTipo() != null) {
                    perfilData.put("fotoBase64", updatedUsuario.getFotoBase64());
                    perfilData.put("fotoTipo", updatedUsuario.getFotoTipo());
                    perfilData.put("fotoCompleta", updatedUsuario.getFotoCompleta());
                }
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Perfil actualizado correctamente",
                    "usuario", perfilData
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al actualizar perfil"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al actualizar perfil: " + e.getMessage()));
        }
    }

    // Endpoint para cambiar contraseña
    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @RequestBody Map<String, String> datos,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Usuario no encontrado"));
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Validar datos
            String currentPassword = datos.get("currentPassword");
            String newPassword = datos.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Contraseña actual y nueva son requeridas"));
            }
            
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "La nueva contraseña debe tener al menos 6 caracteres"));
            }
            
            // Cambiar contraseña
            Optional<Usuario> resultado = usuarioService.changePassword(usuario.getId(), currentPassword, newPassword);
            
            if (resultado.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contraseña cambiada correctamente"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Contraseña actual incorrecta"));
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al cambiar contraseña: " + e.getMessage()));
        }
    }
}