package com.sephora.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

    @JsonProperty("id")
    private int id;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("brand_id")
    private int brandId;

    @JsonProperty("categorie")
    private String categorie;

    @JsonProperty("sous_categorie")
    private String sousCategorie;

    @JsonProperty("prix")
    private double prix;

    @JsonProperty("date_lancement")
    private String dateLancement; // format YYYY-MM-DD

    @JsonProperty("is_vegan")
    private boolean isVegan;

    @JsonProperty("is_bio")
    private boolean isBio;

    // 1. Constructeur vide : INDISPENSABLE pour Jackson
    public Product() {
    }

    // 2. Constructeur avec tous les paramètres (pour ton usage manuel)
    public Product(int id, String nom, int brandId, String categorie, String sousCategorie,
                   double prix, String dateLancement, boolean isVegan, boolean isBio) {
        this.id = id;
        this.nom = nom;
        this.brandId = brandId;
        this.categorie = categorie;
        this.sousCategorie = sousCategorie;
        this.prix = prix;
        this.dateLancement = dateLancement;
        this.isVegan = isVegan;
        this.isBio = isBio;
    }

    // Getters (nécessaires pour la sérialisation Java -> JSON)
    public int getId() { return id; }
    public String getNom() { return nom; }
    public int getBrandId() { return brandId; }
    public String getCategorie() { return categorie; }
    public String getSousCategorie() { return sousCategorie; }
    public double getPrix() { return prix; }
    public String getDateLancement() { return dateLancement; }
    public boolean isVegan() { return isVegan; }
    public boolean isBio() { return isBio; }

    @Override
    public String toString() {
        return "Product [" +
                "ID=" + id +
                ", Nom='" + nom + '\'' +
                ", BrandID=" + brandId +
                ", Catégorie='" + categorie + " / " + sousCategorie + '\'' +
                ", Prix=" + prix + "€" +
                ", Lancé le='" + dateLancement + '\'' +
                ", Vegan=" + (isVegan ? "Oui" : "Non") +
                ", Bio=" + (isBio ? "Oui" : "Non") +
                ']';
    }
}