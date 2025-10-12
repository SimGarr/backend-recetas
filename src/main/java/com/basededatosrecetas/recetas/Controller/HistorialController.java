package com.basededatosrecetas.recetas.Controller;

import com.basededatosrecetas.recetas.Model.Historial;
import com.basededatosrecetas.recetas.Service.HistorialService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @PostMapping
    public Historial addHistorial(@RequestBody Historial historial) {
        return historialService.addHistorial(historial);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Historial> getHistorialByUsuario(@PathVariable Long usuarioId) {
        return historialService.getHistorialByUsuario(usuarioId);
    }

    @GetMapping("/receta/{recetaId}")
    public List<Historial> getHistorialByReceta(@PathVariable Long recetaId) {
        return historialService.getHistorialByReceta(recetaId);
    }
}
