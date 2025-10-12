package com.basededatosrecetas.recetas.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosrecetas.recetas.Model.RecetaEtiqueta;

@Repository
public interface RecetaEtiquetaRepository extends JpaRepository<RecetaEtiqueta, Long> {
    List<RecetaEtiqueta> findByRecetaId(Long recetaId);
    List<RecetaEtiqueta> findByEtiquetaId(Long etiquetaId);

}