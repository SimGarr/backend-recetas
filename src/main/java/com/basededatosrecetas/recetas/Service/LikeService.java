package com.basededatosrecetas.recetas.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Like;
import com.basededatosrecetas.recetas.Repository.LikeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like addLike(Like like) {
        log.info("❤️ Agregando like - Usuario: {}, Receta: {}", 
                like.getUsuario().getId(), like.getReceta().getId());
        try {
            if (likeRepository.existsByUsuarioIdAndRecetaId(like.getUsuario().getId(), like.getReceta().getId())) {
                log.warn("⚠️ Like ya existe - Usuario: {}, Receta: {}", 
                        like.getUsuario().getId(), like.getReceta().getId());
                return null;
            }
            
            Like saved = likeRepository.save(like);
            log.info("✅ Like agregado exitosamente - ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error agregando like - Usuario: {}, Receta: {}, Error: {}", 
                     like.getUsuario().getId(), like.getReceta().getId(), e.getMessage());
            throw e;
        }
    }

    public List<Like> getLikesByUsuario(Long usuarioId) {
        log.debug("👤 Obteniendo likes del usuario - ID: {}", usuarioId);
        List<Like> likes = likeRepository.findByUsuarioId(usuarioId);
        log.info("❤️ Likes del usuario {}: {} recetas", usuarioId, likes.size());
        return likes;
    }

    public List<Like> getLikesByReceta(Long recetaId) {
        log.debug("🍳 Obteniendo likes de receta - ID: {}", recetaId);
        List<Like> likes = likeRepository.findByRecetaId(recetaId);
        log.info("❤️ Likes de receta {}: {} likes", recetaId, likes.size());
        return likes;
    }
}