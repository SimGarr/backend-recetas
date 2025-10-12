package com.basededatosrecetas.recetas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Comentario;
import com.basededatosrecetas.recetas.Repository.ComentarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public Comentario saveComentario(Comentario comentario) {
        log.info("📝 Creando nuevo comentario - Usuario: {}, Receta: {}", 
                comentario.getUsuario().getId(), comentario.getReceta().getId());
        try {
            Comentario saved = comentarioRepository.save(comentario);
            log.info("✅ Comentario creado exitosamente - ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error creando comentario - Usuario: {}, Receta: {}, Error: {}", 
                     comentario.getUsuario().getId(), comentario.getReceta().getId(), e.getMessage());
            throw e;
        }
    }

    public List<Comentario> getAllComentarios() {
        log.debug("📋 Obteniendo todos los comentarios");
        List<Comentario> comentarios = comentarioRepository.findAll();
        log.info("📊 Total de comentarios obtenidos: {}", comentarios.size());
        return comentarios;
    }

    public Optional<Comentario> getComentarioById(Long id) {
        log.debug("🔍 Buscando comentario por ID: {}", id);
        Optional<Comentario> comentario = comentarioRepository.findById(id);
        if (comentario.isPresent()) {
            log.debug("✅ Comentario encontrado - ID: {}", id);
        } else {
            log.warn("⚠️ Comentario no encontrado - ID: {}", id);
        }
        return comentario;
    }

    public List<Comentario> getComentariosByRecetaId(Long recetaId) {
        log.debug("🍳 Obteniendo comentarios de receta - ID: {}", recetaId);
        List<Comentario> comentarios = comentarioRepository.findByRecetaId(recetaId);
        log.info("📊 Comentarios de receta {}: {} comentarios", recetaId, comentarios.size());
        return comentarios;
    }

    public List<Comentario> getComentariosByUsuarioId(Long usuarioId) {
        log.debug("👤 Obteniendo comentarios de usuario - ID: {}", usuarioId);
        List<Comentario> comentarios = comentarioRepository.findByUsuarioId(usuarioId);
        log.info("📊 Comentarios de usuario {}: {} comentarios", usuarioId, comentarios.size());
        return comentarios;
    }

    public void deleteComentario(Long id) {
        log.warn("🗑️ Eliminando comentario - ID: {}", id);
        try {
            if (comentarioRepository.existsById(id)) {
                comentarioRepository.deleteById(id);
                log.info("✅ Comentario eliminado - ID: {}", id);
            } else {
                log.error("❌ Comentario no encontrado para eliminar - ID: {}", id);
            }
        } catch (Exception e) {
            log.error("❌ Error eliminando comentario - ID: {}, Error: {}", id, e.getMessage());
            throw e;
        }
    }
}