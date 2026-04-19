package com.sephora.data.model;

public class Material {
    private int id;
    private int productId;
    private String matiere;
    private String fournisseur;
    private String certification; // BIO, VEGAN, CRUELTY_FREE, NONE

    public Material() {}

    public Material(int id, int productId, String matiere, String fournisseur, String certification) {
        this.id = id;
        this.productId = productId;
        this.matiere = matiere;
        this.fournisseur = fournisseur;
        this.certification = certification;
    }

    // Getters
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public String getMatiere() { return matiere; }
    public String getFournisseur() { return fournisseur; }
    public String getCertification() { return certification; }
}