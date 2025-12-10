package com.basededatosrecetas.recetas.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.basededatosrecetas.recetas.Model.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUsuario_Id(Long usuarioId);

    List<Like> findByReceta_Id(Long recetaId);

    boolean existsByUsuario_IdAndReceta_Id(Long usuarioId, Long recetaId);
}
