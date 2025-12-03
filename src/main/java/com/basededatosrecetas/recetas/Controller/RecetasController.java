package com.basededatosrecetas.recetas.Controller;

import com.basededatosrecetas.recetas.Model.Recetas;
import com.basededatosrecetas.recetas.Service.RecetasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recetas")
@CrossOrigin(origins = "*")
public class RecetasController {

    @Autowired
    private RecetasService recetasService;

    // Obtener todas las recetas
    @GetMapping
    public ResponseEntity<List<Recetas>> getAllRecetas() {
        try {
            List<Recetas> recetas = recetasService.getAllRecetas();
            
            // Procesar imágenes Base64 para la respuesta
            recetas.forEach(receta -> {
                if (receta.getImagenBase64() != null && !receta.getImagenBase64().isEmpty()) {
                    // Si ya tiene URL, no hacemos nada, sino creamos data URL
                    if (receta.getImagenUrl() == null || receta.getImagenUrl().isEmpty()) {
                        receta.setImagenUrl("data:" + receta.getImagenTipo() + ";base64," + receta.getImagenBase64());
                    }
                }
            });
            
            return ResponseEntity.ok(recetas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Recetas> getRecetaById(@PathVariable Long id) {
        return recetasService.getRecetaById(id)
                .map(receta -> {
                    // Procesar imagen para la respuesta
                    if (receta.getImagenBase64() != null && !receta.getImagenBase64().isEmpty()) {
                        if (receta.getImagenUrl() == null || receta.getImagenUrl().isEmpty()) {
                            receta.setImagenUrl("data:" + receta.getImagenTipo() + ";base64," + receta.getImagenBase64());
                        }
                    }
                    return ResponseEntity.ok(receta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener recetas por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Recetas>> getRecetasByCategoria(@PathVariable String categoria) {
        try {
            List<Recetas> recetas = recetasService.getRecetasByCategoria(categoria);
            
            recetas.forEach(receta -> {
                if (receta.getImagenBase64() != null && !receta.getImagenBase64().isEmpty()) {
                    if (receta.getImagenUrl() == null || receta.getImagenUrl().isEmpty()) {
                        receta.setImagenUrl("data:" + receta.getImagenTipo() + ";base64," + receta.getImagenBase64());
                    }
                }
            });
            
            return ResponseEntity.ok(recetas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Crear receta con imágenes Base64
    @PostMapping("/subir")
    public ResponseEntity<?> crearRecetaConImagenes(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("tiempoPreparacion") int tiempoPreparacion,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes) {

        try {
            Recetas receta = new Recetas();
            receta.setNombre(nombre);
            receta.setDescripcion(descripcion);
            receta.setCategoria(categoria);
            receta.setTiempoPreparacion(tiempoPreparacion);
            receta.setUsuarioId(usuarioId);
            receta.setCalificacionPromedio(0.0);

            // Procesar múltiples imágenes (aquí solo guardamos la primera)
            if (imagenes != null && imagenes.length > 0 && !imagenes[0].isEmpty()) {
                MultipartFile imagen = imagenes[0];
                String base64Image = Base64.getEncoder().encodeToString(imagen.getBytes());
                receta.setImagenBase64(base64Image);
                receta.setImagenTipo(imagen.getContentType());
            }

            Recetas recetaGuardada = recetasService.crearReceta(receta);
            
            // Asegurar que la URL esté completa en la respuesta
            if (recetaGuardada.getImagenBase64() != null) {
                recetaGuardada.setImagenUrl("data:" + recetaGuardada.getImagenTipo() + ";base64," + recetaGuardada.getImagenBase64());
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Receta creada exitosamente",
                "receta", recetaGuardada
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // Crear receta con Base64 directo (para Ionic)
    @PostMapping("/subir-base64")
    public ResponseEntity<?> crearRecetaConBase64(@RequestBody Map<String, Object> datos) {
        try {
            Recetas receta = new Recetas();
            receta.setNombre((String) datos.get("nombre"));
            receta.setDescripcion((String) datos.get("descripcion"));
            receta.setCategoria((String) datos.get("categoria"));
            receta.setTiempoPreparacion(Integer.parseInt(datos.get("tiempoPreparacion").toString()));
            receta.setUsuarioId(Long.parseLong(datos.get("usuarioId").toString()));
            receta.setCalificacionPromedio(0.0);
            
            // Manejar imagen Base64
            if (datos.containsKey("imagenBase64") && datos.get("imagenBase64") != null) {
                String base64Data = (String) datos.get("imagenBase64");
                // Si viene con prefijo data:image/...;base64, lo limpiamos
                if (base64Data.contains("base64,")) {
                    base64Data = base64Data.split("base64,")[1];
                }
                receta.setImagenBase64(base64Data);
                receta.setImagenTipo((String) datos.get("imagenTipo"));
            }

            Recetas recetaGuardada = recetasService.crearReceta(receta);
            
            // Asegurar URL completa
            if (recetaGuardada.getImagenBase64() != null) {
                recetaGuardada.setImagenUrl("data:" + recetaGuardada.getImagenTipo() + ";base64," + recetaGuardada.getImagenBase64());
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "receta", recetaGuardada,
                "message", "Receta creada exitosamente"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // Obtener recetas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Recetas>> getRecetasByUsuario(@PathVariable Long usuarioId) {
        try {
            List<Recetas> recetas = recetasService.getAllRecetas().stream()
                    .filter(r -> r.getUsuarioId().equals(usuarioId))
                    .toList();
            
            recetas.forEach(receta -> {
                if (receta.getImagenBase64() != null) {
                    receta.setImagenUrl("data:" + receta.getImagenTipo() + ";base64," + receta.getImagenBase64());
                }
            });
            
            return ResponseEntity.ok(recetas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Actualizar receta
    @PutMapping("/{id}")
    public ResponseEntity<Recetas> updateReceta(@PathVariable Long id, @RequestBody Recetas recetaDetails) {
        return recetasService.updateReceta(id, recetaDetails)
                .map(updatedReceta -> {
                    // Procesar imagen
                    if (updatedReceta.getImagenBase64() != null) {
                        updatedReceta.setImagenUrl("data:" + updatedReceta.getImagenTipo() + ";base64," + updatedReceta.getImagenBase64());
                    }
                    return ResponseEntity.ok(updatedReceta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceta(@PathVariable Long id) {
        boolean deleted = recetasService.deleteReceta(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}