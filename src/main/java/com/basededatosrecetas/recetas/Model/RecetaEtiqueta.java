package com.basededatosrecetas.recetas.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "receta_etiqueta")
public class RecetaEtiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Recetas receta;

    @ManyToOne
    @JoinColumn(name = "etiqueta_id", nullable = false)
    private Etiqueta etiqueta;
}
