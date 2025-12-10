package com.basededatosrecetas.recetas.Repository;

import com.basededatosrecetas.recetas.Model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    
    // Contar comentarios por usuario
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Contar comentarios por receta
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.receta.id = :recetaId")
    Long countByRecetaId(@Param("recetaId") Long recetaId);
    
    // Comentarios por usuario y fecha
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.usuario.id = :usuarioId AND c.fecha >= :fechaDesde")
    Long countByUsuarioIdAndFechaAfter(@Param("usuarioId") Long usuarioId,
                                      @Param("fechaDesde") LocalDateTime fechaDesde);
    
    // Últimos comentarios del usuario
    @Query("SELECT c FROM Comentario c WHERE c.usuario.id = :usuarioId ORDER BY c.fecha DESC")
    List<Comentario> findUltimosComentariosByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Comentarios por receta y usuario
    @Query("SELECT c FROM Comentario c WHERE c.receta.id = :recetaId AND c.usuario.id = :usuarioId")
    List<Comentario> findByRecetaIdAndUsuarioId(@Param("recetaId") Long recetaId,
                                               @Param("usuarioId") Long usuarioId);
}