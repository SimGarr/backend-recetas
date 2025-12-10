package com.basededatosrecetas.recetas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basededatosrecetas.recetas.Model.Favorito;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioId(Long usuarioId);

    List<Favorito> findByRecetaId(Long recetaId);

    void deleteByUsuarioIdAndRecetaId(Long usuarioId, Long recetaId);
    
}
