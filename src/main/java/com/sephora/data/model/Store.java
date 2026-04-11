package com.sephora.data.model;

public class Store {
    private int id;
    private String nom;
    private String adresse;
    private String pays;
    private String region;
    private int surface;
    private String dateOuverture; // format YYYY-MM-DD
    private String statut;
    private int nbEmployes;

    // --- Constructeur avec tous les paramètres ---
    public Store(int id, String nom, String adresse, String pays, String region,
                 int surface, String dateOuverture, String statut, int nbEmployes) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.pays = pays;
        this.region = region;
        this.surface = surface;
        this.dateOuverture = dateOuverture;
        this.statut = statut;
        this.nbEmployes = nbEmployes;
    }

    // --- Getters pour chaque champ ---
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getPays() {
        return pays;
    }

    public String getRegion() {
        return region;
    }

    public int getSurface() {
        return surface;
    }

    public String getDateOuverture() {
        return dateOuverture;
    }

    public String getStatut() {
        return statut;
    }

    public int getNbEmployes() {
        return nbEmployes;
    }

    // --- toString() lisible ---
    @Override
    public String toString() {
        return "Store [" +
                "ID=" + id +
                ", Nom='" + nom + '\'' +
                ", Adresse='" + adresse + '\'' +
                ", Région='" + region + " (" + pays + ")'" +
                ", Surface=" + surface + " m²" +
                ", Ouverture='" + dateOuverture + '\'' +
                ", Statut='" + statut + '\'' +
                ", Employés=" + nbEmployes +
                ']';
    }
}
