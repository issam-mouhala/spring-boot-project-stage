package com.bfios.gei.dashboard.service;

import com.bfios.gei.dashboard.model.*;
import com.bfios.gei.dashboard.model.dto.*;
import com.bfios.gei.dashboard.repository.InMemoryDemandeRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service principal du dashboard.
 *
 * ⚠️ CHANGEMENT : injection de DemandeGeiRepository (JPA) à la place
 * de InMemoryDemandeRepository. Aucune autre modification nécessaire :
 * JpaRepository expose findAll(), save() etc. — mêmes signatures.
 */
@Service
public class DashboardService {

    private final DemandeGeiRepository repository;   // ← JPA au lieu de InMemory
    private final SlaService slaService;

    public DashboardService(DemandeGeiRepository repo, SlaService slaService) {
        this.repository =  repo;
        this.slaService = slaService;
    }

    // ============================================================
    //  Filtrage
    // ============================================================
    public List<DemandeGei> findFiltered(FiltreDto f) {
        return repository.findAll().stream()
                .filter(d -> matchDepartement(d, f))
                .filter(d -> matchNature(d, f))
                .filter(d -> matchProduit(d, f))
                .filter(d -> matchEtat(d, f))
                .filter(d -> matchEtape(d, f))
                .filter(d -> matchCanal(d, f))
                .filter(d -> matchFastTrack(d, f))
                .filter(d -> matchSearch(d, f))
                .filter(d -> matchPeriode(d, f))   // ← NOUVEAU : filtre date
                .toList();
    }

    private boolean matchDepartement(DemandeGei d, FiltreDto f) {
        return f.getDepartement() == null || d.getDepartement() == f.getDepartement();
    }

    private boolean matchNature(DemandeGei d, FiltreDto f) {
        return f.getNatureOperation() == null || f.getNatureOperation().isBlank()
                || d.getNatureOperation().equals(f.getNatureOperation());
    }

    private boolean matchProduit(DemandeGei d, FiltreDto f) {
        return f.getProduitService() == null || f.getProduitService().isBlank()
                || d.getProduitService().equals(f.getProduitService());
    }

    private boolean matchEtat(DemandeGei d, FiltreDto f) {
        return f.getEtat() == null || d.getEtat() == f.getEtat();
    }

    private boolean matchEtape(DemandeGei d, FiltreDto f) {
        return f.getEtape() == null || d.getEtape() == f.getEtape();
    }

    private boolean matchCanal(DemandeGei d, FiltreDto f) {
        return f.getCanal() == null || d.getCanal() == f.getCanal();
    }

    private boolean matchFastTrack(DemandeGei d, FiltreDto f) {
        return f.getFastTrack() == null || d.isFastTrack() == f.getFastTrack();
    }

    private boolean matchSearch(DemandeGei d, FiltreDto f) {
        if (f.getSearch() == null || f.getSearch().isBlank()) return true;
        String q = f.getSearch().toLowerCase();
        String hay = (d.getNumDemande() + " " + d.getIntituleClient() + " " + d.getRadicalClient()+ " "+d.getAgent().toLowerCase());
        return hay.contains(q);
    }

    // ─── NOUVEAU : filtre par plage de dates ───
    private boolean matchPeriode(DemandeGei d, FiltreDto f) {
        if (f.getDateDebut() == null && f.getDateFin() == null) return true;
        LocalDate dt = d.getDateDemande();
        if (dt == null) return false;
        if (f.getDateDebut() != null && dt.isBefore(f.getDateDebut())) return false;
        if (f.getDateFin()   != null && dt.isAfter(f.getDateFin()))   return false;
        return true;
    }

    // ============================================================
    //  KPIs
    // ============================================================
    public KpiDto computeKpis(List<DemandeGei> data) {
        int total = data.size();
        int cloturees = (int) data.stream().filter(d -> d.getEtat() == EtatDemande.CLOTUREE).count();
        int enCours = (int) data.stream().filter(d -> d.getEtat().isEnCours()).count();
        int annulees = (int) data.stream().filter(d -> d.getEtat() == EtatDemande.ANNULLEE).count();
        int slaOk = (int) data.stream().filter(DemandeGei::isSlaOk).count();
        int slaPct = total > 0 ? (int) Math.round((double) slaOk / total * 100) : 0;
        double delaiMoy = total > 0
                ? data.stream().mapToInt(DemandeGei::getDelaiTraitement).average().orElse(0)
                : 0;
        return new KpiDto(total, cloturees, enCours, annulees, slaOk, slaPct,
                Math.round(delaiMoy * 10) / 10.0);
    }

    // ============================================================
    //  TdB2 — Synthèse
    // ============================================================
    public List<LigneSyntheseDto> computeSynthese(List<DemandeGei> data, String dim) {
        Map<String, List<DemandeGei>> groups;
        switch (dim == null ? "dept" : dim) {
            case "nat" -> groups = data.stream().collect(Collectors.groupingBy(
                    d -> d.getNatureOperation() == null ? "(sans nature)" : d.getNatureOperation(),
                    LinkedHashMap::new, Collectors.toList()));
            case "prod" -> groups = data.stream().collect(Collectors.groupingBy(
                    d -> d.getProduitService() == null ? "(sans produit)" : d.getProduitService(),
                    LinkedHashMap::new, Collectors.toList()));
            default -> groups = data.stream().collect(Collectors.groupingBy(
                    d -> d.getDepartement().getLibelle(),
                    LinkedHashMap::new, Collectors.toList()));
        }
        List<LigneSyntheseDto> result = new ArrayList<>();
        for (var entry : groups.entrySet()) {
            LigneSyntheseDto ligne = new LigneSyntheseDto(entry.getKey());
            ligne.setTotal(entry.getValue().size());
            for (DemandeGei d : entry.getValue()) {
                switch (d.getEtat()) {
                                    case EN_CREATION -> ligne.setEnCreation(ligne.getEnCreation() + 1);
                                    case RETOURNEE -> ligne.setRetournee(ligne.getRetournee() + 1);
                                    case EN_COURS_CONTROLE -> ligne.setEnCoursControle(ligne.getEnCoursControle() + 1);
                                    case EN_COURS_TRAITEMENT -> ligne.setEnCoursTraitement(ligne.getEnCoursTraitement() + 1);
                                    case EN_COURS_VALIDATION -> ligne.setEnCoursValidation(ligne.getEnCoursValidation() + 1);
                                    case EN_ATTENTE_DECISION -> ligne.setEnAttenteDecision(ligne.getEnAttenteDecision() + 1);
                                    case EN_ATTENTE_VALIDATION_DECISION -> ligne.setEnAttenteValidationDecision(ligne.getEnAttenteValidationDecision() + 1);
                                    case ANNULEE -> ligne.setAnnulee(ligne.getAnnulee() + 1);
                                    case CLOTUREE -> ligne.setCloturee(ligne.getCloturee() + 1);
                                    default -> throw new IllegalArgumentException("Unexpected value: " + d.getEtat());
                }
            }
            result.add(ligne);
        }
        return result;
    }

    // ============================================================
    //  TdB3 — SLA
    // ============================================================
    public List<DemandeSlaDto> computeSlaList(List<DemandeGei> data) {
        return data.stream().map(slaService::computeSlaDto).toList();
    }

    // ============================================================
    //  Vision Resp A — productivité
    // ============================================================
    public List<ProductiviteAgentDto> computeProductiviteAgents(List<DemandeGei> data) {
        List<String> agents = data.stream()
                .map(DemandeGei::getAgent)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<ProductiviteAgentDto> result = new ArrayList<>();
        for (int i = 0; i < agents.size(); i++) {
            String agent = agents.get(i);
            double mois1 = round2(0.10 + i * 0.05 + pseudoRandom(agent, 1) * 0.10);
            double mois2 = round2(mois1 + (pseudoRandom(agent, 2) - 0.4) * 0.10);
            result.add(new ProductiviteAgentDto(agent, mois1, mois2));
        }
        return result;
    }

    private double pseudoRandom(String seed, int salt) {
        return ((Math.abs(seed.hashCode() + salt * 31) % 100) / 100.0);
    }

    private double round2(double v) { return Math.round(v * 100) / 100.0; }

    public Map<String, Integer> computeTopClients(List<DemandeGei> data) {
        return data.stream()
                .collect(Collectors.groupingBy(DemandeGei::getIntituleClient,
                        Collectors.summingInt(d -> 1)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ============================================================
    //  Vision DG
    // ============================================================
    public List<TopProduitDto> computeTopProduits(List<DemandeGei> data) {
        Map<String, List<DemandeGei>> byProd = data.stream()
                .collect(Collectors.groupingBy(DemandeGei::getProduitService));
        List<TopProduitDto> list = new ArrayList<>();
        for (var entry : byProd.entrySet()) {
            List<DemandeGei> prods = entry.getValue();
            int volume = prods.size();
            double delaiMoy = prods.stream().mapToInt(DemandeGei::getDelaiTraitement).average().orElse(0);
            int slaOk = (int) prods.stream().filter(DemandeGei::isSlaOk).count();
            int slaPct = volume > 0 ? (int) Math.round((double) slaOk / volume * 100) : 0;
            list.add(new TopProduitDto(entry.getKey(), volume,
                    Math.round(delaiMoy * 10) / 10.0, slaPct, 0));
        }
        list.sort(Comparator.comparingInt(TopProduitDto::getVolume).reversed());
        int totalVol = list.stream().mapToInt(TopProduitDto::getVolume).sum();
        if (totalVol > 0) {
            list = list.stream().map(t -> new TopProduitDto(
                    t.getProduit(), t.getVolume(), t.getDelaiMoyen(), t.getSlaPct(),
                    (int) Math.round((double) t.getVolume() / totalVol * 100)
            )).toList();
        }
        return list.stream().limit(8).toList();
    }

    public Map<Departement, Integer> computeSlaByDept(List<DemandeGei> data) {
        Map<Departement, Integer> result = new EnumMap<>(Departement.class);
        for (Departement dept : Departement.values()) {
            List<DemandeGei> subset = data.stream()
                    .filter(d -> d.getDepartement() == dept).toList();
            if (subset.isEmpty()) result.put(dept, 0);
            else {
                long ok = subset.stream().filter(DemandeGei::isSlaOk).count();
                result.put(dept, (int) Math.round((double) ok / subset.size() * 100));
            }
        }
        return result;
    }

    public Map<Departement, Integer> computeVolumeByDept(List<DemandeGei> data) {
        Map<Departement, Integer> result = new EnumMap<>(Departement.class);
        for (Departement dept : Departement.values()) result.put(dept, 0);
        data.forEach(d -> result.merge(d.getDepartement(), 1, Integer::sum));
        return result;
    }

    public Map<EtatDemande, Integer> computeVolumeByEtat(List<DemandeGei> data) {
        Map<EtatDemande, Integer> result = new EnumMap<>(EtatDemande.class);
        for (EtatDemande etat : EtatDemande.values()) result.put(etat, 0);
        data.forEach(d -> result.merge(d.getEtat(), 1, Integer::sum));
        return result;
    }
}
