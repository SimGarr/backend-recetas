package com.basededatosrecetas.recetas.Repository;

import com.basededatosrecetas.recetas.Model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    // Contar favoritos por usuario
    @Query("SELECT COUNT(f) FROM Favorito f WHERE f.usuarioId = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Verificar si receta está en favoritos del usuario
    @Query("SELECT f FROM Favorito f WHERE f.usuarioId = :usuarioId AND f.recetaId = :recetaId")
    Optional<Favorito> findByUsuarioIdAndRecetaId(@Param("usuarioId") Long usuarioId,
                                                 @Param("recetaId") Long recetaId);
    
    // Obtener favoritos del usuario
    @Query("SELECT f FROM Favorito f WHERE f.usuarioId = :usuarioId ORDER BY f.fechaCreacion DESC")
    List<Favorito> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Obtener recetas favoritas del usuario con información
    @Query("SELECT f.recetaId, r.nombre, r.categoria, r.imagenUrl " +
           "FROM Favorito f JOIN Recetas r ON f.recetaId = r.id " +
           "WHERE f.usuarioId = :usuarioId")
    List<Object[]> findFavoritosConInfoByUsuarioId(@Param("usuarioId") Long usuarioId);
}