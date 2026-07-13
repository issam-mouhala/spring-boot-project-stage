package com.bfios.gei.dashboard.controller;

import com.bfios.gei.dashboard.model.Canal;
import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.DemandeGei;
import com.bfios.gei.dashboard.model.EtatDemande;
import com.bfios.gei.dashboard.model.EtapeWorkflow;
import com.bfios.gei.dashboard.model.dto.*;
import com.bfios.gei.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * API REST — Données du dashboard (TdB1 à TdB4 + visions consolidées).
 *
 * Tous les endpoints acceptent les mêmes paramètres de filtrage (query params optionnels) :
 *   - dept (Departement)    : DOMESTIQUE | CREDITS | ADMINISTRATIF | TRADE
 *   - nat  (String)         : libellé nature (ex. "Effet")
 *   - prod (String)         : libellé produit (ex. "Escompte d'effet")
 *   - etat (EtatDemande)    : voir enum
 *   - etape (EtapeWorkflow) : voir enum
 *   - canal (Canal)         : DIGITAL | PHYSIQUE
 *   - fastTrack (Boolean)   : true | false
 *   - search (String)       : recherche libre
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ===== Helper : construire le filtre depuis les query params =====
    private FiltreDto buildFiltre(Departement dept, String nat, String prod,
                                  EtatDemande etat, EtapeWorkflow etape,
                                  Canal canal, Boolean fastTrack, String search,LocalDate dateDebut, LocalDate dateFin) {
        FiltreDto f = new FiltreDto();
        f.setDepartement(dept);
        f.setNatureOperation(nat);
        f.setProduitService(prod);
        f.setEtat(etat);
        f.setEtape(etape);
        f.setCanal(canal);
        f.setFastTrack(fastTrack);
        f.setSearch(search);
        f.setDateDebut(dateDebut);
        f.setDateFin(dateFin);
        return f;
    }

    // ===== TdB1 — Liste détaillée =====
    @GetMapping("/demandes")
    public List<DemandeGei> demandes(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.findFiltered(f);
    }
    @GetMapping("/departementGrouped")
    public List<Object[]> departementGrouped() {
        return  dashboardService.getGroupedDepartement();
    }

    // ===== KPIs (hero) =====
    @GetMapping("/kpis")
    public KpiDto kpis(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.computeKpis(dashboardService.findFiltered(f));
    }

    // ===== TdB2 — Synthèse pivot =====
    @GetMapping("/synthese")
    public List<LigneSyntheseDto> synthese(
            @RequestParam(defaultValue = "dept") String dim,
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.computeSynthese(dashboardService.findFiltered(f), dim);
    }

    // ===== TdB3 — SLA par demande =====
    @GetMapping("/sla")
    public List<DemandeSlaDto> sla(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.computeSlaList(dashboardService.findFiltered(f));
    }

    // ===== TdB4 — Liste avec colonnes CA / CST =====
    // (même donnée que TdB1, exposée séparément pour clarifier l'API)
    @GetMapping("/demandes-ca")
    public List<DemandeGei> demandesCa(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.findFiltered(f);
    }

    // ===== Vision Responsable A — productivité agents =====
    @GetMapping("/responsable-a/productivite")
    public List<ProductiviteAgentDto> productiviteAgents(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.computeProductiviteAgents(dashboardService.findFiltered(f));
    }

    @GetMapping("/responsable-a/top-clients")
    public Map<String, Integer> topClients(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        return dashboardService.computeTopClients(dashboardService.findFiltered(f));
    }

    // ===== Vision DG — synthèse globale =====
    @GetMapping("/dg/summary")
    public Map<String, Object> dgSummary(
            @RequestParam(required = false) Departement dept,
            @RequestParam(required = false) String nat,
            @RequestParam(required = false) String prod,
            @RequestParam(required = false) EtatDemande etat,
            @RequestParam(required = false) EtapeWorkflow etape,
            @RequestParam(required = false) Canal canal,
            @RequestParam(required = false) Boolean fastTrack,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        FiltreDto f = buildFiltre(dept, nat, prod, etat, etape, canal, fastTrack, search,dateDebut,dateFin);
        List<DemandeGei> data = dashboardService.findFiltered(f);
        KpiDto kpis = dashboardService.computeKpis(data);

        return Map.of(
                "kpis", kpis,
                "topProduits", dashboardService.computeTopProduits(data),
                "slaByDept", dashboardService.computeSlaByDept(data),
                "volumeByDept", dashboardService.computeVolumeByDept(data),
                "volumeByEtat", dashboardService.computeVolumeByEtat(data)
        );
    }
}
