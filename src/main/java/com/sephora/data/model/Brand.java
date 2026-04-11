package com.sephora.data.model;

public class Brand {
    private int id;
    private String nom;
    private String groupe;
    private String categorie;
    private String datePartenariat; // format YYYY-MM-DD
    private String paysOrigine;

    // Constructeur avec tous les paramètres
    public Brand(int id, String nom, String groupe, String categorie, String datePartenariat, String paysOrigine) {
        this.id = id;
        this.nom = nom;
        this.groupe = groupe;
        this.categorie = categorie;
        this.datePartenariat = datePartenariat;
        this.paysOrigine = paysOrigine;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getGroupe() { return groupe; }
    public String getCategorie() { return categorie; }
    public String getDatePartenariat() { return datePartenariat; }
    public String getPaysOrigine() { return paysOrigine; }

    @Override
    public String toString() {
        return "Brand [" +
                "ID=" + id +
                ", Nom='" + nom + '\'' +
                ", Groupe='" + groupe + '\'' +
                ", Catégorie='" + categorie + '\'' +
                ", Partenariat='" + datePartenariat + '\'' +
                ", Origine='" + paysOrigine + '\'' +
                ']';
    }
}
