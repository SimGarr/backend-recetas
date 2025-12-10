package com.basededatosrecetas.recetas.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basededatosrecetas.recetas.Model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

Optional<Usuario> findByEmail(String email);

}
