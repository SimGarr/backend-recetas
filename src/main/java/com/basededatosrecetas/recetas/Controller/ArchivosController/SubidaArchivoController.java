package com.basededatosrecetas.recetas.Controller.ArchivosController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.basededatosrecetas.recetas.Model.Recetas;
import com.basededatosrecetas.recetas.Service.RecetasService;

@RestController
@RequestMapping("/api/subida")
@CrossOrigin(origins = "*")
public class SubidaArchivoController {

    @Value("${directorio.subidas:subidas}")
    private String directorioSubidas;

    @Autowired
    private RecetasService servicioRecetas;

    @PostMapping("/receta")
    public ResponseEntity<?> subirArchivoReceta(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("tiempoPreparacion") int tiempoPreparacion,
            Authentication authentication) {
        
        // Verificar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }
        
        try {
            // Validar archivo
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            // Validar tipo de archivo
            String tipoContenido = archivo.getContentType();
            boolean esImagen = tipoContenido != null && tipoContenido.startsWith("image/");
            boolean esVideo = tipoContenido != null && tipoContenido.startsWith("video/");
            
            if (!esImagen && !esVideo) {
                return ResponseEntity.badRequest().body("Solo se permiten imágenes y videos");
            }

            // Validar tamaño del archivo (máximo 50MB)
            if (archivo.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("El archivo es demasiado grande. Máximo 50MB");
            }

            // Crear directorio si no existe
            Path rutaSubidas = Paths.get(directorioSubidas);
            if (!Files.exists(rutaSubidas)) {
                Files.createDirectories(rutaSubidas);
            }

            // Generar nombre único para el archivo
            String extension = "";
            if (tipoContenido != null) {
                if (tipoContenido.equals("image/jpeg")) extension = ".jpg";
                else if (tipoContenido.equals("image/png")) extension = ".png";
                else if (tipoContenido.equals("video/mp4")) extension = ".mp4";
                else if (tipoContenido.equals("video/avi")) extension = ".avi";
                else extension = "." + archivo.getOriginalFilename()
                    .substring(archivo.getOriginalFilename().lastIndexOf(".") + 1);
            }

            String nombreArchivoUnico = UUID.randomUUID().toString() + extension;
            Path rutaArchivo = rutaSubidas.resolve(nombreArchivoUnico);

            // Guardar archivo
            Files.copy(archivo.getInputStream(), rutaArchivo);

            // Obtener información del usuario autenticado
            String nombreUsuario = authentication.getName();
            
            // Crear la receta
            Recetas receta = new Recetas();
            receta.setNombre(nombre);
            receta.setDescripcion(descripcion);
            receta.setCategoria(categoria);
            receta.setTiempoPreparacion(tiempoPreparacion);
            receta.setImagenUrl("/api/archivos/" + nombreArchivoUnico);
            receta.setNombreArchivo(archivo.getOriginalFilename());
            receta.setTipoArchivo(esImagen ? "imagen" : "video");
            receta.setTamañoArchivo(archivo.getSize());
            
            // Puedes agregar el usuario que creó la receta si tu modelo lo soporta
            // receta.setUsuarioCreador(nombreUsuario);

            Recetas recetaGuardada = servicioRecetas.crearReceta(receta);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Receta subida exitosamente");
            respuesta.put("receta", recetaGuardada);
            respuesta.put("urlArchivo", "/api/archivos/" + nombreArchivoUnico);
            respuesta.put("usuario", nombreUsuario);

            return ResponseEntity.ok(respuesta);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body("Error al guardar el archivo: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error interno del servidor: " + e.getMessage());
        }
    }
    
    // Endpoint adicional para verificar permisos
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarPermisos(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("autenticado", true);
        respuesta.put("usuario", authentication.getName());
        respuesta.put("roles", authentication.getAuthorities());
        
        return ResponseEntity.ok(respuesta);
    }
}