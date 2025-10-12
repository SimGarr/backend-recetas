package com.basededatosrecetas.recetas.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Historial;
import com.basededatosrecetas.recetas.Repository.HistorialRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HistorialService {

    private final HistorialRepository historialRepository;

    public HistorialService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public Historial addHistorial(Historial historial) {
        log.info("👀 Agregando al historial - Usuario: {}, Receta: {}", 
                historial.getUsuario().getId(), historial.getReceta().getId());
        try {
            Historial saved = historialRepository.save(historial);
            log.debug("✅ Historial registrado - ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error registrando historial - Usuario: {}, Receta: {}, Error: {}", 
                     historial.getUsuario().getId(), historial.getReceta().getId(), e.getMessage());
            throw e;
        }
    }

    public List<Historial> getHistorialByUsuario(Long usuarioId) {
        log.debug("👤 Obteniendo historial del usuario - ID: {}", usuarioId);
        List<Historial> historial = historialRepository.findByUsuarioId(usuarioId);
        log.info("📊 Historial del usuario {}: {} registros", usuarioId, historial.size());
        return historial;
    }

    public List<Historial> getHistorialByReceta(Long recetaId) {
        log.debug("🍳 Obteniendo historial de receta - ID: {}", recetaId);
        List<Historial> historial = historialRepository.findByRecetaId(recetaId);
        log.info("📊 Historial de receta {}: {} visualizaciones", recetaId, historial.size());
        return historial;
    }
}