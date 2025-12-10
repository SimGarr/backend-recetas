package com.basededatosrecetas.recetas.Model;


import lombok.Data;

@Data
public class PerfilDTO {
    private String nombre;
    private String descripcionPerfil;
    private String fotoPerfilBase64;
    private String fotoPerfilTipo;
}