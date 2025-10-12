package com.basededatosrecetas.recetas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basededatosrecetas.recetas.Model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByRecetaId(Long recetaId);
    List<Comentario> findByUsuarioId(Long usuarioId);
}
