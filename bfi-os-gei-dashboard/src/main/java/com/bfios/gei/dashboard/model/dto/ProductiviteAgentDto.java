package com.bfios.gei.dashboard.model.dto;

/**
 * Productivité d'un agent (vision Responsable A).
 */
public class ProductiviteAgentDto {

    private String agent;
    private double mois1;
    private double mois2;
    private double variation;
    private double variationPct;
    private String tendance;    // "up" / "down"

    public ProductiviteAgentDto() {
    }

    public ProductiviteAgentDto(String agent, double mois1, double mois2) {
        this.agent = agent;
        this.mois1 = mois1;
        this.mois2 = mois2;
        this.variation = mois2 - mois1;
        this.variationPct = mois1 != 0 ? (variation / mois1) * 100 : 0;
        this.tendance = variation >= 0 ? "up" : "down";
    }

    public String getAgent() { return agent; }
    public double getMois1() { return mois1; }
    public double getMois2() { return mois2; }
    public double getVariation() { return variation; }
    public double getVariationPct() { return variationPct; }
    public String getTendance() { return tendance; }
}
