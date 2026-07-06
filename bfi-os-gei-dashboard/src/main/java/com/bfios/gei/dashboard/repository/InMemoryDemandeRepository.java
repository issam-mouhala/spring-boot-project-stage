package com.bfios.gei.dashboard.repository;

import com.bfios.gei.dashboard.model.Canal;
import com.bfios.gei.dashboard.model.DemandeGei;
import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.EtatDemande;
import com.bfios.gei.dashboard.model.EtapeWorkflow;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Repository en mémoire des demandes GEI.
 *
 * Pour la production : remplacer par un repository JPA (Spring Data)
 * branché à PostgreSQL/OnBase. L'interface reste identique.
 */
@Repository
public class InMemoryDemandeRepository {

    private final List<DemandeGei> demandes = new CopyOnWriteArrayList<>();

    public InMemoryDemandeRepository() {
        seed();
    }

    public List<DemandeGei> findAll() {
        return Collections.unmodifiableList(demandes);
    }

    public Optional<DemandeGei> findByNum(String numDemande) {
        return demandes.stream().filter(d -> d.getNumDemande().equals(numDemande)).findFirst();
    }

    public void save(DemandeGei d) {
        demandes.removeIf(existing -> existing.getNumDemande().equals(d.getNumDemande()));
        demandes.add(d);
    }

    public void saveAll(List<DemandeGei> list) {
        list.forEach(this::save);
    }

    // ============================================================
    //  Données d'exemple (22 demandes — cohérentes avec la maquette)
    // ============================================================
    private void seed() {
        save(new DemandeGei("200000052", Departement.DOMESTIQUE, "Monétique", "Recalcule du code PIN",
                "0000000", "Intitulé 1", true, LocalDate.of(2026, 4, 12),
                EtatDemande.EN_CREATION, EtapeWorkflow.CA_AC_INITIATION, 1, Canal.DIGITAL,
                "CA-01", "Agent 3", 1, true));

        save(new DemandeGei("200000051", Departement.ADMINISTRATIF, "BP SCAN", "Télécollecte (Scanner chèque)",
                "1111111", "Intitulé 2", true, LocalDate.of(2026, 4, 12),
                EtatDemande.EN_CREATION, EtapeWorkflow.CA_AC_INITIATION, 1, Canal.DIGITAL,
                "CA-02", "Agent 4", 1, true));

        save(new DemandeGei("200000050", Departement.CREDITS, "Mise en place des lignes", "MEP des lignes",
                "-", "Intitulé 3", false, LocalDate.of(2026, 4, 12),
                EtatDemande.RETOURNEE, EtapeWorkflow.CA_AC_INITIATION, 2, Canal.DIGITAL,
                "CA-03", "agent 1", 3, false));

        save(new DemandeGei("200000049", Departement.TRADE, "Gestion des titres d'importation", "Titres d'importation",
                "-", "Intitulé 4", false, LocalDate.of(2026, 4, 12),
                EtatDemande.EN_COURS_CONTROLE, EtapeWorkflow.CHEF_D_CONTROLE, 1, Canal.PHYSIQUE,
                "CA-04", "Agent 2", 2, true));

        save(new DemandeGei("200000048", Departement.TRADE, "Transfert", "Transfert international (commercial / Financier émis)",
                "-", "Intitulé 5", false, LocalDate.of(2026, 4, 12),
                EtatDemande.EN_COURS_CONTROLE, EtapeWorkflow.CHEF_D_CONTROLE, 1, Canal.DIGITAL,
                "CA-02", "Agent 2", 2, true));

        save(new DemandeGei("200000047", Departement.DOMESTIQUE, "Chèque", "Certification de chèque",
                "0111111", "Intitulé 6", true, LocalDate.of(2026, 4, 13),
                EtatDemande.EN_COURS_TRAITEMENT, EtapeWorkflow.VERIFICATION, 1, Canal.PHYSIQUE,
                "CA-01", "Agent 3", 1, true));

        save(new DemandeGei("200000046", Departement.DOMESTIQUE, "Effet", "Encaissement effet",
                "0101010", "Intitulé 7", true, LocalDate.of(2026, 4, 13),
                EtatDemande.EN_COURS_TRAITEMENT, EtapeWorkflow.VERIFICATION, 1, Canal.PHYSIQUE,
                "CA-01", "Agent 3", 2, true));

        save(new DemandeGei("200000045", Departement.DOMESTIQUE, "Effet", "Escompte d'effet",
                "1010101", "Intitulé 8", true, LocalDate.of(2026, 4, 13),
                EtatDemande.EN_COURS_TRAITEMENT, EtapeWorkflow.VERIFICATION, 1, Canal.PHYSIQUE,
                "CA-05", "Agent 3", 2, true));

        save(new DemandeGei("200000044", Departement.TRADE, "Garanties", "Garanties internationales reçues",
                "1000101", "Intitulé 9", false, LocalDate.of(2026, 4, 13),
                EtatDemande.RETOURNEE, EtapeWorkflow.VERIFICATION, 2, Canal.PHYSIQUE,
                "CA-04", "Agent 2", 4, false));

        save(new DemandeGei("200000043", Departement.TRADE, "Garanties", "Caution des opérations sous régimes économiques en douane",
                "0000111", "Intitulé 10", false, LocalDate.of(2026, 4, 13),
                EtatDemande.RETOURNEE, EtapeWorkflow.VERIFICATION, 2, Canal.DIGITAL,
                "CA-04", "Agent 2", 4, false));

        save(new DemandeGei("200000042", Departement.CREDITS, "Financement MLT/ court terme", "Remboursement Anticipé",
                "0000001", "Intitulé 11", false, LocalDate.of(2026, 4, 13),
                EtatDemande.EN_COURS_VALIDATION, EtapeWorkflow.VALIDATION, 1, Canal.DIGITAL,
                "CA-03", "Agent4", 3, true));

        save(new DemandeGei("200000041", Departement.ADMINISTRATIF, "Placement", "Attestation de situations des DAT",
                "1000000", "Intitulé 12", true, LocalDate.of(2026, 4, 13),
                EtatDemande.EN_COURS_VALIDATION, EtapeWorkflow.VALIDATION, 2, Canal.DIGITAL,
                "CA-02", "Agent 4", 2, true));

        save(new DemandeGei("200000040", Departement.DOMESTIQUE, "Chèque", "Certification de chèque",
                "-", "Intitulé 13", false, LocalDate.of(2026, 4, 13),
                EtatDemande.EN_COURS_VALIDATION, EtapeWorkflow.VALIDATION, 1, Canal.DIGITAL,
                "CA-01", "Agent 3", 2, true));

        save(new DemandeGei("200000039", Departement.DOMESTIQUE, "Mouvement de Fonds", "Virement de Masse",
                "-", "Intitulé 14", false, LocalDate.of(2026, 4, 14),
                EtatDemande.EN_ATTENTE_DECISION, EtapeWorkflow.CA_DECISION, 1, Canal.DIGITAL,
                "CA-01", "Agent 5", 3, true));

        save(new DemandeGei("200000038", Departement.DOMESTIQUE, "Chèque", "Demande de carnet de chèque",
                "1000000", "Intitulé 15", true, LocalDate.of(2026, 4, 14),
                EtatDemande.EN_ATTENTE_DECISION, EtapeWorkflow.CA_DECISION, 1, Canal.DIGITAL,
                "CA-05", "Agent 5", 2, true));

        save(new DemandeGei("200000037", Departement.DOMESTIQUE, "Effet", "Demande de Restitution d'effets avant sa date d'échéance",
                "1001000", "Intitulé 16", true, LocalDate.of(2026, 4, 14),
                EtatDemande.EN_ATTENTE_VALIDATION_DECISION, EtapeWorkflow.DMO_VALIDATION, 1, Canal.DIGITAL,
                "CA-01", "Agent 6", 3, true));

        save(new DemandeGei("200000036", Departement.DOMESTIQUE, "Mouvement de Fonds", "Virement Unitaire (RTGS, Virement normal)",
                "0011001", "Intitulé 17", true, LocalDate.of(2026, 4, 14),
                EtatDemande.ANNULLEE, EtapeWorkflow.HORS_WORKFLOW, 1, Canal.PHYSIQUE,
                "CA-01", "Agent 3", 1, true));

        save(new DemandeGei("200000035", Departement.CREDITS, "Gestion de compte (Crédits)", "Fusion en intérêt",
                "1100110", "Intitulé 18", true, LocalDate.of(2026, 4, 14),
                EtatDemande.CLOTUREE, EtapeWorkflow.HORS_WORKFLOW, 1, Canal.PHYSIQUE,
                "CA-03", "agent 1", 2, true));

        save(new DemandeGei("200000034", Departement.ADMINISTRATIF, "Digital Banking", "TRADENET",
                "0000111", "Intitulé 19", true, LocalDate.of(2026, 4, 14),
                EtatDemande.CLOTUREE, EtapeWorkflow.HORS_WORKFLOW, 1, Canal.PHYSIQUE,
                "CA-02", "Agent 4", 1, true));

        save(new DemandeGei("200000033", Departement.DOMESTIQUE, "Effet", "Escompte d'effet",
                "1010101", "Intitulé 20", true, LocalDate.of(2026, 4, 14),
                EtatDemande.CLOTUREE, EtapeWorkflow.HORS_WORKFLOW, 1, Canal.DIGITAL,
                "CA-05", "Agent 6", 2, true));

        save(new DemandeGei("200000032", Departement.TRADE, "CREDOC", "Credoc Export",
                "1001100", "Intitulé 21", false, LocalDate.of(2026, 4, 14),
                EtatDemande.EN_COURS_CONTROLE, EtapeWorkflow.CHEF_D_CONTROLE, 1, Canal.DIGITAL,
                "CA-04", "Agent 2", 2, true));

        save(new DemandeGei("200000031", Departement.CREDITS, "Financement MLT/ court terme", "SPOT",
                "0110011", "Intitulé 22", false, LocalDate.of(2026, 4, 14),
                EtatDemande.EN_COURS_TRAITEMENT, EtapeWorkflow.VERIFICATION, 1, Canal.PHYSIQUE,
                "CA-03", "agent 1", 3, false));
    }
}
