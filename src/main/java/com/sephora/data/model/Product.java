package com.sephora.data.model;

public class Product {
    private int id;
    private String nom;
    private int brandId;
    private String categorie;
    private String sousCategorie;
    private double prix;
    private String dateLancement; // format YYYY-MM-DD
    private boolean isVegan;
    private boolean isBio;

    // Constructeur avec tous les paramètres
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

    // Getters
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
