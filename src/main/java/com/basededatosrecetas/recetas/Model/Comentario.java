package com.basededatosrecetas.recetas.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Recetas receta;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}
