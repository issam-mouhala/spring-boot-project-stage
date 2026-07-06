package com.bfios.gei.dashboard.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Demande GEI unitaire traitée sur OnBase.
 * Entité centrale du modèle de données, mappée sur la table DEMANDE_GEI.
 *
 * Annotations JPA :
 *  - @Entity              → cette classe est une entité persistée
 *  - @Table(name=...)     → nom de la table en base
 *  - @Id                  → clé primaire
 *  - @Enumerated(STRING)  → l'enum est stockée par son nom (lisible en base)
 *  - @Column(name=...)    → mapping explicite colonne SQL
 */
@Entity
@Table(name = "DEMANDE_GEI")
public class DemandeGei {

    @Id
    @Column(name = "num_demande", length = 12, nullable = false)
    private String numDemande;

    @Enumerated(EnumType.STRING)
    @Column(name = "departement", length = 20, nullable = false)
    private Departement departement;

    @Column(name = "nature_operation", length = 100, nullable = false)
    private String natureOperation;

    @Column(name = "produit_service", length = 250, nullable = false)
    private String produitService;

    @Column(name = "radical_client", length = 20)
    private String radicalClient;

    @Column(name = "intitule_client", length = 150)
    private String intituleClient;

    @Column(name = "fast_track", nullable = false)
    private boolean fastTrack;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", length = 40, nullable = false)
    private EtatDemande etat;

    @Enumerated(EnumType.STRING)
    @Column(name = "etape", length = 40, nullable = false)
    private EtapeWorkflow etape;

    @Column(name = "nb_iterations", nullable = false)
    private int nbIterations;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", length = 10, nullable = false)
    private Canal canal;

    @Column(name = "montant", precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "charge_affaires", length = 20)
    private String chargeAffaires;

    @Column(name = "agent", length = 50)
    private String agent;

    @Column(name = "delai_traitement", nullable = false)
    private int delaiTraitement;

    @Column(name = "sla_ok", nullable = false)
    private boolean slaOk;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ─── Hooks JPA pour timestamps automatiques ───
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ─── Constructeurs ───
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

    // ─── Getters / Setters ───
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

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getChargeAffaires() { return chargeAffaires; }
    public void setChargeAffaires(String chargeAffaires) { this.chargeAffaires = chargeAffaires; }

    public String getAgent() { return agent; }
    public void setAgent(String agent) { this.agent = agent; }

    public int getDelaiTraitement() { return delaiTraitement; }
    public void setDelaiTraitement(int delaiTraitement) { this.delaiTraitement = delaiTraitement; }

    public boolean isSlaOk() { return slaOk; }
    public void setSlaOk(boolean slaOk) { this.slaOk = slaOk; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

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
