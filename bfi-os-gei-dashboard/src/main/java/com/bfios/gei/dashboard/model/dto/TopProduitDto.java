package com.bfios.gei.dashboard.model.dto;

/**
 * Ligne top produits (vision DG).
 */
public class TopProduitDto {

    private String produit;
    private int volume;
    private double delaiMoyen;
    private int slaPct;
    private int partVolumePct;

    public TopProduitDto() {
    }

    public TopProduitDto(String produit, int volume, double delaiMoyen, int slaPct, int partVolumePct) {
        this.produit = produit;
        this.volume = volume;
        this.delaiMoyen = delaiMoyen;
        this.slaPct = slaPct;
        this.partVolumePct = partVolumePct;
    }

    public String getProduit() { return produit; }
    public int getVolume() { return volume; }
    public double getDelaiMoyen() { return delaiMoyen; }
    public int getSlaPct() { return slaPct; }
    public int getPartVolumePct() { return partVolumePct; }
}
