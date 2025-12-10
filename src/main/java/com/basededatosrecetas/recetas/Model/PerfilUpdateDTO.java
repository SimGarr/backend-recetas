package com.basededatosrecetas.recetas.Model;



import lombok.Data;

@Data
public class PerfilUpdateDTO {
    private String nombre;
    private String descripcion;
    private String imagenPerfilBase64;  // Base64 sin prefijo
    private String imagenPerfilTipo;
}