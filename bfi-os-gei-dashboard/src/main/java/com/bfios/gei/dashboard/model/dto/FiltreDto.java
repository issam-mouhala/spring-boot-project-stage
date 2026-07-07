package com.bfios.gei.dashboard.model.dto;

import com.bfios.gei.dashboard.model.Canal;
import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.EtatDemande;
import com.bfios.gei.dashboard.model.EtapeWorkflow;

import java.time.LocalDate;

/**
 * Critères de filtrage des demandes.
 * Tous les champs sont optionnels (null = pas de filtre sur ce critère).
 */
public class FiltreDto {

    private Departement departement;
    private String natureOperation;
    private String produitService;
    private EtatDemande etat;
    private EtapeWorkflow etape;
    private Canal canal;
    private Boolean fastTrack;
    private String search;

    // ─── NOUVEAU : plage de dates pour le filtre période ───
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Getters / Setters

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }

    public String getNatureOperation() { return natureOperation; }
    public void setNatureOperation(String natureOperation) { this.natureOperation = natureOperation; }

    public String getProduitService() { return produitService; }
    public void setProduitService(String produitService) { this.produitService = produitService; }

    public EtatDemande getEtat() { return etat; }
    public void setEtat(EtatDemande etat) { this.etat = etat; }

    public EtapeWorkflow getEtape() { return etape; }
    public void setEtape(EtapeWorkflow etape) { this.etape = etape; }

    public Canal getCanal() { return canal; }
    public void setCanal(Canal canal) { this.canal = canal; }

    public Boolean getFastTrack() { return fastTrack; }
    public void setFastTrack(Boolean fastTrack) { this.fastTrack = fastTrack; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public String getMontant() { return search; }
    public void setMontant(String search) { this.search = search; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
