package com.basededatosrecetas.recetas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Etiqueta;
import com.basededatosrecetas.recetas.Model.RecetaEtiqueta;
import com.basededatosrecetas.recetas.Repository.EtiquetaRepository;
import com.basededatosrecetas.recetas.Repository.RecetaEtiquetaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final RecetaEtiquetaRepository recetaEtiquetaRepository;

    public EtiquetaService(EtiquetaRepository etiquetaRepository, RecetaEtiquetaRepository recetaEtiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
        this.recetaEtiquetaRepository = recetaEtiquetaRepository;
    }

    public Etiqueta saveEtiqueta(Etiqueta etiqueta) {
        log.info("🏷️ Creando nueva etiqueta - Nombre: {}", etiqueta.getNombre());
        try {
            Etiqueta saved = etiquetaRepository.save(etiqueta);
            log.info("✅ Etiqueta creada exitosamente - ID: {}, Nombre: {}", saved.getId(), saved.getNombre());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error creando etiqueta - Nombre: {}, Error: {}", etiqueta.getNombre(), e.getMessage());
            throw e;
        }
    }

    public List<Etiqueta> getAllEtiquetas() {
        log.debug("📋 Obteniendo todas las etiquetas");
        List<Etiqueta> etiquetas = etiquetaRepository.findAll();
        log.info("📊 Total de etiquetas obtenidas: {}", etiquetas.size());
        return etiquetas;
    }

    public Optional<Etiqueta> getEtiquetaById(Long id) {
        log.debug("🔍 Buscando etiqueta por ID: {}", id);
        Optional<Etiqueta> etiqueta = etiquetaRepository.findById(id);
        if (etiqueta.isPresent()) {
            log.debug("✅ Etiqueta encontrada - ID: {}, Nombre: {}", id, etiqueta.get().getNombre());
        } else {
            log.warn("⚠️ Etiqueta no encontrada - ID: {}", id);
        }
        return etiqueta;
    }

    public void deleteEtiqueta(Long id) {
        log.warn("🗑️ Eliminando etiqueta - ID: {}", id);
        try {
            if (etiquetaRepository.existsById(id)) {
                etiquetaRepository.deleteById(id);
                log.info("✅ Etiqueta eliminada - ID: {}", id);
            } else {
                log.error("❌ Etiqueta no encontrada para eliminar - ID: {}", id);
            }
        } catch (Exception e) {
            log.error("❌ Error eliminando etiqueta - ID: {}, Error: {}", id, e.getMessage());
            throw e;
        }
    }

    public RecetaEtiqueta addEtiquetaToReceta(RecetaEtiqueta recetaEtiqueta) {
        log.info("🔗 Agregando etiqueta a receta - Receta: {}, Etiqueta: {}", 
                recetaEtiqueta.getReceta().getId(), recetaEtiqueta.getEtiqueta().getId());
        try {
            RecetaEtiqueta saved = recetaEtiquetaRepository.save(recetaEtiqueta);
            log.info("✅ Etiqueta agregada a receta exitosamente - ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error agregando etiqueta a receta - Receta: {}, Etiqueta: {}, Error: {}", 
                     recetaEtiqueta.getReceta().getId(), recetaEtiqueta.getEtiqueta().getId(), e.getMessage());
            throw e;
        }
    }

    public List<RecetaEtiqueta> getEtiquetasByReceta(Long recetaId) {
        log.debug("🍳 Obteniendo etiquetas de receta - ID: {}", recetaId);
        List<RecetaEtiqueta> etiquetas = recetaEtiquetaRepository.findByRecetaId(recetaId);
        log.info("📊 Etiquetas de receta {}: {} etiquetas", recetaId, etiquetas.size());
        return etiquetas;
    }

    public List<RecetaEtiqueta> getRecetasByEtiqueta(Long etiquetaId) {
        log.debug("🏷️ Obteniendo recetas por etiqueta - ID: {}", etiquetaId);
        List<RecetaEtiqueta> recetas = recetaEtiquetaRepository.findByEtiquetaId(etiquetaId);
        log.info("📊 Recetas con etiqueta {}: {} recetas", etiquetaId, recetas.size());
        return recetas;
    }
}