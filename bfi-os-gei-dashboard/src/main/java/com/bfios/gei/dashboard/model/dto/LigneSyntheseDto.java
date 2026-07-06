package com.bfios.gei.dashboard.model.dto;

/**
 * Ligne de synthèse pivot (TdB2) : dimension × 9 états.
 */
public class LigneSyntheseDto {

    private String dimension;       // ex. "Domestique"
    private int total;
    private int enCreation;
    private int retournee;
    private int enCoursControle;
    private int enCoursTraitement;
    private int enCoursValidation;
    private int enAttenteDecision;
    private int enAttenteValidationDecision;
    private int annulee;
    private int cloturee;

    public LigneSyntheseDto() {
    }

    public LigneSyntheseDto(String dimension) {
        this.dimension = dimension;
    }

    // Getters / Setters (tous en int)

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getEnCreation() { return enCreation; }
    public void setEnCreation(int v) { this.enCreation = v; }
    public int getRetournee() { return retournee; }
    public void setRetournee(int v) { this.retournee = v; }
    public int getEnCoursControle() { return enCoursControle; }
    public void setEnCoursControle(int v) { this.enCoursControle = v; }
    public int getEnCoursTraitement() { return enCoursTraitement; }
    public void setEnCoursTraitement(int v) { this.enCoursTraitement = v; }
    public int getEnCoursValidation() { return enCoursValidation; }
    public void setEnCoursValidation(int v) { this.enCoursValidation = v; }
    public int getEnAttenteDecision() { return enAttenteDecision; }
    public void setEnAttenteDecision(int v) { this.enAttenteDecision = v; }
    public int getEnAttenteValidationDecision() { return enAttenteValidationDecision; }
    public void setEnAttenteValidationDecision(int v) { this.enAttenteValidationDecision = v; }
    public int getAnnulee() { return annulee; }
    public void setAnnulee(int v) { this.annulee = v; }
    public int getCloturee() { return cloturee; }
    public void setCloturee(int v) { this.cloturee = v; }
}
