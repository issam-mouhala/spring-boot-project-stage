package com.bfios.gei.dashboard;

import com.bfios.gei.dashboard.model.Canal;
import com.bfios.gei.dashboard.model.DemandeGeiRepository;
import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.EtatDemande;
import com.bfios.gei.dashboard.model.EtapeWorkflow;
import com.bfios.gei.dashboard.model.dto.FiltreDto;
import com.bfios.gei.dashboard.model.dto.KpiDto;
import com.bfios.gei.dashboard.service.DashboardService;
import com.bfios.gei.dashboard.service.ReferenceDataService;
import com.bfios.gei.dashboard.service.SlaService;
import com.bfios.gei.dashboard.repository.InMemoryDemandeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DashboardServiceTests {

    private final InMemoryDemandeRepository repo = new InMemoryDemandeRepository();
    private final SlaService slaService = new SlaService();
    private final DashboardService service = new DashboardService((DemandeGeiRepository) repo, slaService);
    private final ReferenceDataService refService = new ReferenceDataService();

    @Test
    void repository_shouldSeed22Demandes() {
        assertEquals(22, repo.findAll().size());
    }

    @Test
    void kpis_shouldComputeCorrectly() {
        KpiDto kpis = service.computeKpis(repo.findAll());
        assertEquals(22, kpis.getTotal());
        assertTrue(kpis.getCloturees() >= 2, "Au moins 2 demandes clôturées attendues");
        assertTrue(kpis.getSlaPct() >= 0 && kpis.getSlaPct() <= 100);
        assertTrue(kpis.getDelaiMoyen() > 0);
    }

    @Test
    void filter_byDepartementTrade_shouldReturnOnlyTrade() {
        FiltreDto f = new FiltreDto();
        f.setDepartement(Departement.TRADE);
        List<?> result = service.findFiltered(f);
        assertTrue(result.stream().allMatch(d ->
                ((com.bfios.gei.dashboard.model.DemandeGei) d).getDepartement() == Departement.TRADE));
    }

    @Test
    void filter_byCanalDigital_shouldReturnOnlyDigital() {
        FiltreDto f = new FiltreDto();
        f.setCanal(Canal.DIGITAL);
        List<?> result = service.findFiltered(f);
        assertTrue(result.stream().allMatch(d ->
                ((com.bfios.gei.dashboard.model.DemandeGei) d).getCanal() == Canal.DIGITAL));
    }

    @Test
    void filter_byFastTrackTrue_shouldReturnOnlyFastTrack() {
        FiltreDto f = new FiltreDto();
        f.setFastTrack(true);
        List<?> result = service.findFiltered(f);
        assertTrue(result.stream().allMatch(d ->
                ((com.bfios.gei.dashboard.model.DemandeGei) d).isFastTrack()));
    }

    @Test
    void filter_byEtatCloturee_shouldReturnOnlyCloturees() {
        FiltreDto f = new FiltreDto();
        f.setEtat(EtatDemande.CLOTUREE);
        List<?> result = service.findFiltered(f);
        assertTrue(result.stream().allMatch(d ->
                ((com.bfios.gei.dashboard.model.DemandeGei) d).getEtat() == EtatDemande.CLOTUREE));
    }

    @Test
    void filter_search_shouldMatchNumDemande() {
        FiltreDto f = new FiltreDto();
        f.setSearch("200000047");
        List<?> result = service.findFiltered(f);
        assertEquals(1, result.size());
    }

    @Test
    void synthese_byDept_shouldReturn4Groups() {
        var result = service.computeSynthese(repo.findAll(), "dept");
        assertEquals(4, result.size());
        int totalSum = result.stream().mapToInt(l -> l.getTotal()).sum();
        assertEquals(22, totalSum);
    }

    @Test
    void sla_shouldCompute6StagesPerDemande() {
        var demandes = repo.findAll();
        var slaList = service.computeSlaList(demandes);
        assertEquals(demandes.size(), slaList.size());
        // Chaque demande a 6 stages (les 6 étapes actives du workflow)
        slaList.forEach(sla -> assertEquals(6, sla.getStages().size()));
        // Le total est >= 0
        slaList.forEach(sla -> assertTrue(sla.getTotal() >= 0));
    }

    @Test
    void referenceData_shouldReturn4Departements() {
        assertEquals(4, refService.getDepartements().size());
    }

    @Test
    void referenceData_naturesForDomestique_shouldContainEffet() {
        assertTrue(refService.getNatures(Departement.DOMESTIQUE).contains("Effet"));
    }

    @Test
    void referenceData_produitsForDomestiqueEffet_shouldContainEscompte() {
        List<String> produits = refService.getProduits(Departement.DOMESTIQUE, "Effet");
        assertTrue(produits.contains("Escompte d'effet"));
        assertTrue(produits.contains("Encaissement effet"));
    }

    @Test
    void topProduits_shouldReturnSortedByVolumeDesc() {
        var result = service.computeTopProduits(repo.findAll());
        assertTrue(result.size() > 0);
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).getVolume() >= result.get(i).getVolume(),
                    "Top produits doit être trié par volume décroissant");
        }
    }

    @Test
    void slaByDept_shouldReturnPercentagesFor4Depts() {
        var result = service.computeSlaByDept(repo.findAll());
        assertEquals(4, result.size());
        result.values().forEach(pct -> assertTrue(pct >= 0 && pct <= 100));
    }

    @Test
    void productivite_shouldReturnOneRowPerAgent() {
        var result = service.computeProductiviteAgents(repo.findAll());
        long distinctAgents = repo.findAll().stream()
                .map(d -> d.getAgent()).distinct().count();
        assertEquals(distinctAgents, result.size());
    }
}
