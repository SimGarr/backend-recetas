package com.basededatosrecetas.recetas.Controller;



import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosrecetas.recetas.Model.Favorito;
import com.basededatosrecetas.recetas.Service.FavoritoService;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Favorito> obtenerFavoritosUsuario(@PathVariable Long usuarioId) {
        return favoritoService.getFavoritosByUsuario(usuarioId);
    }

    @PostMapping
    public Favorito agregarFavorito(@RequestBody Favorito favorito) {
        return favoritoService.agregarFavorito(favorito);
    }

    @DeleteMapping
    public void eliminarFavorito(@RequestParam Long usuarioId, @RequestParam Long recetaId) {
        favoritoService.eliminarFavorito(usuarioId, recetaId);
    }
}
