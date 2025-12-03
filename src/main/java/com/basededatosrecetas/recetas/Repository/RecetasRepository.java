package com.basededatosrecetas.recetas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basededatosrecetas.recetas.Model.Recetas;

public interface RecetasRepository extends JpaRepository<Recetas, Long> {
    List<Recetas> findByCategoria(String categoria);
    List<Recetas> findByUsuarioId(Long usuarioId);
}
