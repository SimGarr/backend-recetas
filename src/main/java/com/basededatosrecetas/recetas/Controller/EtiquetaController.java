package com.basededatosrecetas.recetas.Controller;

import com.basededatosrecetas.recetas.Model.Etiqueta;
import com.basededatosrecetas.recetas.Model.RecetaEtiqueta;
import com.basededatosrecetas.recetas.Service.EtiquetaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/etiquetas")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    public EtiquetaController(EtiquetaService etiquetaService) {
        this.etiquetaService = etiquetaService;
    }

    @PostMapping
    public Etiqueta createEtiqueta(@RequestBody Etiqueta etiqueta) {
        return etiquetaService.saveEtiqueta(etiqueta);
    }

    @GetMapping
    public List<Etiqueta> getAllEtiquetas() {
        return etiquetaService.getAllEtiquetas();
    }

    @GetMapping("/{id}")
    public Optional<Etiqueta> getEtiqueta(@PathVariable Long id) {
        return etiquetaService.getEtiquetaById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEtiqueta(@PathVariable Long id) {
        etiquetaService.deleteEtiqueta(id);
    }

    @PostMapping("/receta")
    public RecetaEtiqueta addEtiquetaToReceta(@RequestBody RecetaEtiqueta recetaEtiqueta) {
        return etiquetaService.addEtiquetaToReceta(recetaEtiqueta);
    }

    @GetMapping("/receta/{recetaId}")
    public List<RecetaEtiqueta> getEtiquetasByReceta(@PathVariable Long recetaId) {
        return etiquetaService.getEtiquetasByReceta(recetaId);
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    public List<RecetaEtiqueta> getRecetasByEtiqueta(@PathVariable Long etiquetaId) {
        return etiquetaService.getRecetasByEtiqueta(etiquetaId);
    }
}
