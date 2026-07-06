package com.bfios.gei.dashboard.service;

import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.ReferenceNode;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service des données référentielles : Département → Nature → Produit.
 * Hiérarchie en cascade alimentant les filtres de l'UI.
 *
 * Source des valeurs : onglet "Filtres TdBs" du fichier Excel source.
 */
@Service
public class ReferenceDataService {

    private static final Map<Departement, List<String>> NATURES = new LinkedHashMap<>();
    private static final Map<String, List<String>> PRODUITS = new LinkedHashMap<>();

    static {
        // Natures par département
        NATURES.put(Departement.DOMESTIQUE, Arrays.asList(
                "Crédits", "Effet", "Chèque", "Mouvement de Fonds", "Monétique",
                "Change", "Gestion de compte en dirhams"));
        NATURES.put(Departement.CREDITS, Arrays.asList(
                "Mise en place des lignes", "Financement MLT", "Financement court Terme",
                "Autres", "Gestion de compte", "Garantie", "Cautions",
                "Financement MLT/ court terme", "Gestion de compte (Crédits)"));
        NATURES.put(Departement.ADMINISTRATIF, Arrays.asList(
                "Administratif", "BP SCAN", "Digital Banking", "Mouvement de flux", "Placement"));
        NATURES.put(Departement.TRADE, Arrays.asList(
                "Transfert", "CREDOC", "REMDOC", "Garanties", "Financement Trade",
                "Gestion des titres d'importation", "OC"));

        // Produits par département + nature (clé = "DEPT::NAT")
        PRODUITS.put("Domestique::Effet", Arrays.asList(
                "Escompte d'effet", "Encaissement effet", "Demande Lettre LCN",
                "Demande de carnet d'effet", "Retour d'effets impayés",
                "Demande de représentation", "Opposition au paiement effet",
                "Prorogation de l'échéance",
                "Demande de Restitution d'effets avant sa date d'échéance"));
        PRODUITS.put("Domestique::Mise en place des lignes", List.of("MEP des lignes"));
        PRODUITS.put("Domestique::Chèque", Arrays.asList(
                "Chèque de banque", "Certification de chèque", "Demande Lettre chèque",
                "Crédit immédiat", "Encaissement chèque", "Demande de carnet de chèque",
                "Opposition au paiement de chèques", "Retour de chèque impayé",
                "Demande d'avis de sort"));
        PRODUITS.put("Domestique::Mouvement de Fonds", Arrays.asList(
                "Télérèglement (CNSS, Impôt, Douane, Vignette)", "Mise à disposition unitaire",
                "Virement de Masse", "Virement Unitaire (RTGS, Virement normal)",
                "Mise à disposition de Masse", "Ramassage de Fond", "Appel de fond",
                "Virement MT101"));
        PRODUITS.put("Domestique::Monétique", Arrays.asList(
                "Création carte", "Opposition carte", "Recalcule du code PIN"));
        PRODUITS.put("Domestique::Change", List.of("Commande de devises"));
        PRODUITS.put("Domestique::Gestion de compte en dirhams", Arrays.asList(
                "Demande de Blocage de comptes (saisies arrêts et saisies conservatoires)"));

        PRODUITS.put("Crédits::Mise en place des lignes", List.of("MEP des lignes"));
        PRODUITS.put("Crédits::Financement MLT/ court terme", Arrays.asList(
                "Crédit Moyen Long terme", "Remboursement Anticipé", "SPOT",
                "Facilité de caisse", "Découvert", "Aval", "Avance sur Marché nanti",
                "Préfinancement Marché public", "Avance sur crédit TVA",
                "Avance sur droit de douane"));
        PRODUITS.put("Crédits::Gestion de compte (Crédits)", Arrays.asList(
                "Fusion en intérêt", "Fusion en solde"));
        PRODUITS.put("Crédits::Gestion de compte", Arrays.asList(
                "Ouverture / Fermeture de compte", "Cash pooling mono société / Cash pooling Groupe",
                "Délivrance d'attestation de solde", "Mise à jours du dossier juridique",
                "Délivrance d'attestation bancaires"));

        PRODUITS.put("Administratif::BP SCAN", List.of("Télécollecte (Scanner chèque)"));
        PRODUITS.put("Administratif::Digital Banking", Arrays.asList(
                "BP E-Corporate", "BP connect", "BP corporate payment plateform",
                "BP Transdoc", "GATEWAY", "BP PAYTRACKER", "TRADENET", "BP TRADE REPORT"));
        PRODUITS.put("Administratif::Placement", Arrays.asList(
                "Souscription DAT", "Attestation de situations des DAT"));

        PRODUITS.put("Trade::Transfert", Arrays.asList(
                "Transfert international (commercial / Financier émis)",
                "Transfert international (reçu)"));
        PRODUITS.put("Trade::CREDOC", Arrays.asList("Credoc Export", "Crédoc Import"));
        PRODUITS.put("Trade::REMDOC", Arrays.asList(
                "Remise documentaire export", "Remise documentaire import"));
        PRODUITS.put("Trade::Garanties", Arrays.asList(
                "Lettres de crédit stand by émises et reçues", "Garanties internationales",
                "Avals Crédit fournisseurs en Etranger", "Aval Etrangers en devises",
                "Shipping guarantees",
                "Caution des opérations sous régimes économiques en douane",
                "Crédits d'enlèvement", "Obligations cautionnées", "Autres cautions douanières",
                "Garanties internationales reçues"));
        PRODUITS.put("Trade::Financement Trade", Arrays.asList(
                "Avance sur créances nées à l'étranger",
                "Mobilisation des créances nées en devises à l'export avec ou sans recours",
                "Préfinancement export en devises", "Refinancement import en devises",
                "Escompte sans recours (Forfaiting)", "Préfinancement export en dirhams",
                "Refinancement import en dirhams"));
        PRODUITS.put("Trade::Gestion des titres d'importation",
                List.of("Titres d'importation"));
        PRODUITS.put("Trade::OC", List.of("Demande autorisation office de changes"));
    }

    public List<Departement> getDepartements() {
        return Arrays.asList(Departement.values());
    }

    public List<String> getNatures(Departement dept) {
        if (dept == null) return List.of();
        return NATURES.getOrDefault(dept, List.of());
    }

    public List<String> getProduits(Departement dept, String nature) {
        if (dept == null || nature == null) return List.of();
        String key = dept.getLibelle() + "::" + nature;
        return PRODUITS.getOrDefault(key, List.of());
    }

    /** Vue arborescente complète (pour /api/referentiel/arbre). */
    public List<ReferenceNode> getArbre() {
        return getDepartements().stream()
                .map(dept -> new ReferenceNode(
                        dept.name(),
                        dept.getLibelle(),
                        getNatures(dept)))
                .toList();
    }
}
