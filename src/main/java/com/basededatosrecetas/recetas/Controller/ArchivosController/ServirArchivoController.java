package com.basededatosrecetas.recetas.Controller.ArchivosController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*")
public class ServirArchivoController {

    @Value("${directorio.subidas:subidas}")
    private String directorioSubidas;

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> servirArchivo(@PathVariable String nombreArchivo) {
        try {
            // Validar que el nombre del archivo no contenga rutas relativas
            if (nombreArchivo.contains("..")) {
                return ResponseEntity.badRequest().build();
            }
            
            Path rutaArchivo = Paths.get(directorioSubidas).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                // Determinar el tipo de contenido
                String tipoContenido = determinarTipoContenido(nombreArchivo);
                
                // Configurar headers para caché (opcional)
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                           "inline; filename=\"" + recurso.getFilename() + "\"");
                headers.add(HttpHeaders.CACHE_CONTROL, "public, max-age=3600"); // 1 hora de caché

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType(tipoContenido))
                        .body(recurso);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private String determinarTipoContenido(String nombreArchivo) {
        String nombreLower = nombreArchivo.toLowerCase();
        
        if (nombreLower.endsWith(".jpg") || nombreLower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (nombreLower.endsWith(".png")) {
            return "image/png";
        } else if (nombreLower.endsWith(".gif")) {
            return "image/gif";
        } else if (nombreLower.endsWith(".mp4")) {
            return "video/mp4";
        } else if (nombreLower.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (nombreLower.endsWith(".mov")) {
            return "video/quicktime";
        } else if (nombreLower.endsWith(".webm")) {
            return "video/webm";
        } else {
            return "application/octet-stream";
        }
    }
    
    // Endpoint para verificar si un archivo existe
    @GetMapping("/{nombreArchivo:.+}/existe")
    public ResponseEntity<?> verificarArchivoExiste(@PathVariable String nombreArchivo) {
        try {
            if (nombreArchivo.contains("..")) {
                return ResponseEntity.badRequest().body("Nombre de archivo inválido");
            }
            
            Path rutaArchivo = Paths.get(directorioSubidas).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("existe", recurso.exists() && recurso.isReadable());
            respuesta.put("nombreArchivo", nombreArchivo);
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("existe", false);
            error.put("error", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }
}