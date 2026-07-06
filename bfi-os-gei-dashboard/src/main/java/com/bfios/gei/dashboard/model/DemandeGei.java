package com.bfios.gei.dashboard.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Demande GEI unitaire traitée sur OnBase.
 * Entité centrale du modèle de données.
 */
public class DemandeGei {

    private String numDemande;          // ex. "200000052"
    private Departement departement;
    private String natureOperation;     // libellé libre (ex. "Monétique")
    private String produitService;      // libellé libre (ex. "Recalcule du code PIN")
    private String radicalClient;       // ex. "0000000" ou "-"
    private String intituleClient;      // ex. "Intitulé 1"
    private boolean fastTrack;          // OUI / NON
    private LocalDate dateDemande;
    private EtatDemande etat;
    private EtapeWorkflow etape;
    private int nbIterations;
    private Canal canal;
    private Double montant;
    private String chargeAffaires;      // ex. "CA-01"
    private String agent;               // ex. "Agent 3"
    private int delaiTraitement;        // en jours
    private boolean slaOk;              // respect global du SLA

    public DemandeGei() {
    }

    public DemandeGei(String numDemande, Departement departement, String natureOperation,
                      String produitService, String radicalClient, String intituleClient,
                      boolean fastTrack, LocalDate dateDemande, EtatDemande etat,
                      EtapeWorkflow etape, int nbIterations, Canal canal,
                      String chargeAffaires, String agent, int delaiTraitement, boolean slaOk) {
        this.numDemande = numDemande;
        this.departement = departement;
        this.natureOperation = natureOperation;
        this.produitService = produitService;
        this.radicalClient = radicalClient;
        this.intituleClient = intituleClient;
        this.fastTrack = fastTrack;
        this.dateDemande = dateDemande;
        this.etat = etat;
        this.etape = etape;
        this.nbIterations = nbIterations;
        this.canal = canal;
        this.chargeAffaires = chargeAffaires;
        this.agent = agent;
        this.delaiTraitement = delaiTraitement;
        this.slaOk = slaOk;
    }

    // ===== Getters / Setters =====

    public String getNumDemande() { return numDemande; }
    public void setNumDemande(String numDemande) { this.numDemande = numDemande; }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }

    public String getNatureOperation() { return natureOperation; }
    public void setNatureOperation(String natureOperation) { this.natureOperation = natureOperation; }

    public String getProduitService() { return produitService; }
    public void setProduitService(String produitService) { this.produitService = produitService; }

    public String getRadicalClient() { return radicalClient; }
    public void setRadicalClient(String radicalClient) { this.radicalClient = radicalClient; }

    public String getIntituleClient() { return intituleClient; }
    public void setIntituleClient(String intituleClient) { this.intituleClient = intituleClient; }

    public boolean isFastTrack() { return fastTrack; }
    public void setFastTrack(boolean fastTrack) { this.fastTrack = fastTrack; }

    public LocalDate getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }

    public EtatDemande getEtat() { return etat; }
    public void setEtat(EtatDemande etat) { this.etat = etat; }

    public EtapeWorkflow getEtape() { return etape; }
    public void setEtape(EtapeWorkflow etape) { this.etape = etape; }

    public int getNbIterations() { return nbIterations; }
    public void setNbIterations(int nbIterations) { this.nbIterations = nbIterations; }

    public Canal getCanal() { return canal; }
    public void setCanal(Canal canal) { this.canal = canal; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getChargeAffaires() { return chargeAffaires; }
    public void setChargeAffaires(String chargeAffaires) { this.chargeAffaires = chargeAffaires; }

    public String getAgent() { return agent; }
    public void setAgent(String agent) { this.agent = agent; }

    public int getDelaiTraitement() { return delaiTraitement; }
    public void setDelaiTraitement(int delaiTraitement) { this.delaiTraitement = delaiTraitement; }

    public boolean isSlaOk() { return slaOk; }
    public void setSlaOk(boolean slaOk) { this.slaOk = slaOk; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DemandeGei that = (DemandeGei) o;
        return Objects.equals(numDemande, that.numDemande);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numDemande);
    }
}
