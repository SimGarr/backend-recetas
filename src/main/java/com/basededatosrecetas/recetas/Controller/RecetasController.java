package com.basededatosrecetas.recetas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosrecetas.recetas.Model.Recetas;
import com.basededatosrecetas.recetas.Service.RecetasService;

@RestController
@RequestMapping("/api/recetas")
@CrossOrigin(origins = "*") // Permite que Angular/Ionic consuma la API
public class RecetasController {

    @Autowired
    private RecetasService recetasService;

    // Obtener todas las recetas
    @GetMapping
    public List<Recetas> getAllRecetas() {
        return recetasService.getAllRecetas();
    }

    // Obtener receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Recetas> getRecetaById(@PathVariable Long id) {
        return recetasService.getRecetaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener recetas por categoría
    @GetMapping("/categoria/{categoria}")
    public List<Recetas> getRecetasByCategoria(@PathVariable String categoria) {
        return recetasService.getRecetasByCategoria(categoria);
    }

    // Crear nueva receta
    @PostMapping
    public Recetas crearReceta(@RequestBody Recetas receta) {
        return recetasService.crearReceta(receta);
    }

    // Actualizar receta
    @PutMapping("/{id}")
    public ResponseEntity<Recetas> updateReceta(@PathVariable Long id, @RequestBody Recetas recetaDetails) {
        return recetasService.updateReceta(id, recetaDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceta(@PathVariable Long id) {
        boolean deleted = recetasService.deleteReceta(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
