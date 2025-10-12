package com.basededatosrecetas.recetas.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosrecetas.recetas.Model.Historial;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {

    // Obtiene el historial de un usuario específico
    List<Historial> findByUsuarioId(Long usuarioId);

    // Obtiene todas las visitas de una receta específica
    List<Historial> findByRecetaId(Long recetaId);
}
