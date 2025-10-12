package com.basededatosrecetas.recetas.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.basededatosrecetas.recetas.Model.Favorito;
import com.basededatosrecetas.recetas.Repository.FavoritoRepository;

@Service
public class FavoritoService {

    private static final Logger logger = LoggerFactory.getLogger(FavoritoService.class);

    private final FavoritoRepository favoritoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository) {
        this.favoritoRepository = favoritoRepository;
    }

    public List<Favorito> getFavoritosByUsuario(Long usuarioId) {
        logger.info("📂 Obteniendo favoritos del usuario con ID {}", usuarioId);
        List<Favorito> favoritos = favoritoRepository.findByUsuarioId(usuarioId);
        logger.info("✅ Se encontraron {} favoritos", favoritos.size());
        return favoritos;
    }

    public Favorito agregarFavorito(Favorito favorito) {
        logger.info("➕ Agregando favorito: usuarioId={}, recetaId={}", favorito.getUsuarioId(), favorito.getRecetaId());
        Favorito nuevoFavorito = favoritoRepository.save(favorito);
        logger.info("✅ Favorito agregado correctamente: {}", nuevoFavorito);
        return nuevoFavorito;
    }

    public void eliminarFavorito(Long usuarioId, Long recetaId) {
        logger.info("🗑️ Eliminando favorito: usuarioId={}, recetaId={}", usuarioId, recetaId);
        favoritoRepository.deleteByUsuarioIdAndRecetaId(usuarioId, recetaId);
        logger.info("✅ Favorito eliminado correctamente");
    }
}
