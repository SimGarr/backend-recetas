package com.basededatosrecetas.recetas.Repository;

import com.basededatosrecetas.recetas.Model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // Contar likes por usuario
    @Query("SELECT COUNT(l) FROM Like l WHERE l.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Contar likes por receta
    @Query("SELECT COUNT(l) FROM Like l WHERE l.receta.id = :recetaId")
    Long countByRecetaId(@Param("recetaId") Long recetaId);
    
    // Verificar si usuario dio like a receta
    @Query("SELECT l FROM Like l WHERE l.usuario.id = :usuarioId AND l.receta.id = :recetaId")
    Optional<Like> findByUsuarioIdAndRecetaId(@Param("usuarioId") Long usuarioId, 
                                             @Param("recetaId") Long recetaId);
    
    // Likes recientes del usuario
    @Query("SELECT l FROM Like l WHERE l.usuario.id = :usuarioId AND l.fecha >= :fechaDesde")
    List<Like> findByUsuarioIdAndFechaAfter(@Param("usuarioId") Long usuarioId,
                                           @Param("fechaDesde") LocalDateTime fechaDesde);
    
    // Obtener recetas más likes del usuario
    @Query("SELECT l.receta.id, l.receta.nombre, COUNT(l) as total " +
           "FROM Like l WHERE l.usuario.id = :usuarioId " +
           "GROUP BY l.receta.id, l.receta.nombre " +
           "ORDER BY total DESC")
    List<Object[]> findTopRecetasLikedByUsuario(@Param("usuarioId") Long usuarioId);
}