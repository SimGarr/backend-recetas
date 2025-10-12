package com.basededatosrecetas.recetas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Recetas;
import com.basededatosrecetas.recetas.Repository.RecetasRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecetasService {

    @Autowired
    private RecetasRepository recetasRepository;

    public List<Recetas> getAllRecetas() {
        log.debug("📋 Obteniendo todas las recetas");
        List<Recetas> recetas = recetasRepository.findAll();
        log.info("🍳 Total de recetas obtenidas: {}", recetas.size());
        return recetas;
    }

    public List<Recetas> getRecetasByCategoria(String categoria) {
        log.debug("📂 Obteniendo recetas por categoría: {}", categoria);
        List<Recetas> recetas = recetasRepository.findByCategoria(categoria);
        log.info("📊 Recetas en categoría '{}': {} recetas", categoria, recetas.size());
        return recetas;
    }

    public Optional<Recetas> getRecetaById(Long id) {
        log.debug("🔍 Buscando receta por ID: {}", id);
        Optional<Recetas> receta = recetasRepository.findById(id);
        if (receta.isPresent()) {
            log.debug("✅ Receta encontrada - ID: {}, Nombre: {}", id, receta.get().getNombre());
        } else {
            log.warn("⚠️ Receta no encontrada - ID: {}", id);
        }
        return receta;
    }

    public Recetas createReceta(Recetas receta) {
        log.info("🆕 Creando nueva receta - Nombre: {}, Categoría: {}", 
                receta.getNombre(), receta.getCategoria());
        try {
            Recetas saved = recetasRepository.save(receta);
            log.info("✅ Receta creada exitosamente - ID: {}, Nombre: {}", saved.getId(), saved.getNombre());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error creando receta - Nombre: {}, Error: {}", receta.getNombre(), e.getMessage());
            throw e;
        }
    }

    public Optional<Recetas> updateReceta(Long id, Recetas recetaDetails) {
        log.info("✏️ Actualizando receta - ID: {}", id);
        try {
            Optional<Recetas> updated = recetasRepository.findById(id).map(receta -> {
                log.debug("📝 Campos a actualizar - Nombre: {}, Categoría: {}, Tiempo: {}min", 
                         recetaDetails.getNombre(), recetaDetails.getCategoria(), recetaDetails.getTiempoPreparacion());
                
                receta.setNombre(recetaDetails.getNombre());
                receta.setDescripcion(recetaDetails.getDescripcion());
                receta.setCategoria(recetaDetails.getCategoria());
                receta.setImagenUrl(recetaDetails.getImagenUrl());
                receta.setTiempoPreparacion(recetaDetails.getTiempoPreparacion());
                receta.setCalificacionPromedio(recetaDetails.getCalificacionPromedio());
                
                Recetas saved = recetasRepository.save(receta);
                log.info("✅ Receta actualizada exitosamente - ID: {}", saved.getId());
                return saved;
            });
            
            if (updated.isEmpty()) {
                log.warn("⚠️ Receta no encontrada para actualizar - ID: {}", id);
            }
            
            return updated;
        } catch (Exception e) {
            log.error("❌ Error actualizando receta - ID: {}, Error: {}", id, e.getMessage());
            throw e;
        }
    }

    public boolean deleteReceta(Long id) {
        log.warn("🗑️ Eliminando receta - ID: {}", id);
        try {
            boolean deleted = recetasRepository.findById(id).map(receta -> {
                recetasRepository.delete(receta);
                log.info("✅ Receta eliminada - ID: {}, Nombre: {}", id, receta.getNombre());
                return true;
            }).orElse(false);
            
            if (!deleted) {
                log.error("❌ Receta no encontrada para eliminar - ID: {}", id);
            }
            
            return deleted;
        } catch (Exception e) {
            log.error("❌ Error eliminando receta - ID: {}, Error: {}", id, e.getMessage());
            throw e;
        }
    }
}