package com.bfios.gei.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller minimal pour servir la page d'accueil.
 *
 * Avec Spring Boot, les fichiers statiques sous /static sont servis
 * automatiquement à la racine. Ce controller garantit que "/" retourne
 * bien index.html même en cas d'absence de fichier par défaut.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
