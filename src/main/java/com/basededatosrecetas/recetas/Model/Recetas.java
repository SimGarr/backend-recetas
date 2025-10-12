package com.basededatosrecetas.recetas.Model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = true)
    private String imagenUrl;

    @Column(nullable = false)
    private int tiempoPreparacion; // en minutos

    @Column(nullable = true)
    private double calificacionPromedio;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}