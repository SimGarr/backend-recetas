package com.basededatosrecetas.recetas.Controller;



import com.basededatosrecetas.recetas.Model.Comentario;
import com.basededatosrecetas.recetas.Service.ComentarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping
    public Comentario addComentario(@RequestBody Comentario comentario) {
        return comentarioService.saveComentario(comentario);
    }

    @GetMapping
    public List<Comentario> getAllComentarios() {
        return comentarioService.getAllComentarios();
    }

    @GetMapping("/{id}")
    public Optional<Comentario> getComentario(@PathVariable Long id) {
        return comentarioService.getComentarioById(id);
    }

    @GetMapping("/receta/{recetaId}")
    public List<Comentario> getComentariosByReceta(@PathVariable Long recetaId) {
        return comentarioService.getComentariosByRecetaId(recetaId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Comentario> getComentariosByUsuario(@PathVariable Long usuarioId) {
        return comentarioService.getComentariosByUsuarioId(usuarioId);
    }

    @DeleteMapping("/{id}")
    public void deleteComentario(@PathVariable Long id) {
        comentarioService.deleteComentario(id);
    }
}
