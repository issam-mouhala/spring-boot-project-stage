package com.bfios.gei.dashboard.controller;

import com.bfios.gei.dashboard.model.Canal;
import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.EtatDemande;
import com.bfios.gei.dashboard.model.EtapeWorkflow;
import com.bfios.gei.dashboard.service.ReferenceDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * API REST — Données référentielles (départements, natures, produits, états, étapes).
 * Alimente les listes déroulantes des filtres en cascade de l'UI.
 */
@RestController
@RequestMapping("/api/referentiel")
public class ReferentielApiController {

    private final ReferenceDataService refService;

    public ReferentielApiController(ReferenceDataService refService) {
        this.refService = refService;
    }

    @GetMapping("/departements")
    public List<Departement> departements() {
        return refService.getDepartements();
    }

    @GetMapping("/natures")
    public List<String> natures(@RequestParam(required = false) Departement dept) {
        return refService.getNatures(dept);
    }

    @GetMapping("/produits")
    public List<String> produits(@RequestParam(required = false) Departement dept,
                                 @RequestParam(required = false) String nature) {
        return refService.getProduits(dept, nature);
    }

    @GetMapping("/etats")
    public List<EtatDemande> etats() {
        return Arrays.asList(EtatDemande.values());
    }

    @GetMapping("/etapes")
    public List<EtapeWorkflow> etapes() {
        return Arrays.asList(EtapeWorkflow.values());
    }

    @GetMapping("/canals")
    public List<Canal> canals() {
        return Arrays.asList(Canal.values());
    }

    @GetMapping("/arbre")
    public Object arbre() {
        return Map.of(
                "departements", refService.getArbre(),
                "etats", etats(),
                "etapes", etapes(),
                "canals", canals()
        );
    }
}
