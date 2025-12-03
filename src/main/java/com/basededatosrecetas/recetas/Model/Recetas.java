package com.basededatosrecetas.recetas.Model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "recetas")
public class Recetas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private String categoria;

    @Column(name = "tiempo_preparacion", nullable = false)
    private int tiempoPreparacion;

    @Column(name = "calificacion_promedio")
    private double calificacionPromedio = 0.0;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // Campo para almacenar Base64
    @Lob
    @Column(name = "imagen_base64", length = 10485760)
    private String imagenBase64;

    @Column(name = "imagen_tipo")
    private String imagenTipo;

    // Campo normal para URL (lo completaremos cuando sea necesario)
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    // Método para obtener la imagen completa
    public String getImagenCompleta() {
        if (imagenBase64 != null && imagenTipo != null) {
            return "data:" + imagenTipo + ";base64," + imagenBase64;
        }
        return imagenUrl; // Si no hay Base64, retornamos la URL normal
    }

    // Método para verificar si tiene imagen
    public boolean tieneImagen() {
        return (imagenBase64 != null && !imagenBase64.isEmpty()) || 
               (imagenUrl != null && !imagenUrl.isEmpty());
    }
}