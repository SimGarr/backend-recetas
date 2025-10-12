package com.basededatosrecetas.recetas.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosrecetas.recetas.Model.Like;
import com.basededatosrecetas.recetas.Service.LikeService;

@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public Like addLike(@RequestBody Like like) {
        return likeService.addLike(like);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Like> getLikesByUsuario(@PathVariable Long usuarioId) {
        return likeService.getLikesByUsuario(usuarioId);
    }

    @GetMapping("/receta/{recetaId}")
    public List<Like> getLikesByReceta(@PathVariable Long recetaId) {
        return likeService.getLikesByReceta(recetaId);
    }
}
