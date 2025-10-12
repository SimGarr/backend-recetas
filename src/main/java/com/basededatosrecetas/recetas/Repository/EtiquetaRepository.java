package com.basededatosrecetas.recetas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basededatosrecetas.recetas.Model.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    Etiqueta findByNombre(String nombre);
    
}
