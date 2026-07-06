package com.bfios.gei.dashboard.model.dto;

/**
 * KPIs principaux affichés en hero des vues opérationnelles.
 */
public class KpiDto {

    private int total;
    private int cloturees;
    private int enCours;
    private int annulees;
    private int slaOk;
    private int slaPct;
    private double delaiMoyen;

    public KpiDto() {
    }

    public KpiDto(int total, int cloturees, int enCours, int annulees,
                  int slaOk, int slaPct, double delaiMoyen) {
        this.total = total;
        this.cloturees = cloturees;
        this.enCours = enCours;
        this.annulees = annulees;
        this.slaOk = slaOk;
        this.slaPct = slaPct;
        this.delaiMoyen = delaiMoyen;
    }

    public int getTotal() { return total; }
    public int getCloturees() { return cloturees; }
    public int getEnCours() { return enCours; }
    public int getAnnulees() { return annulees; }
    public int getSlaOk() { return slaOk; }
    public int getSlaPct() { return slaPct; }
    public double getDelaiMoyen() { return delaiMoyen; }
}
