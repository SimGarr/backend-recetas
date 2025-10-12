package com.basededatosrecetas.recetas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosrecetas.recetas.Model.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUsuarioId(Long usuarioId);

    List<Like> findByRecetaId(Long recetaId);

    boolean existsByUsuarioIdAndRecetaId(Long usuarioId, Long recetaId);
}
