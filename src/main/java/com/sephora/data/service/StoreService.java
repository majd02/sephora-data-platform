package com.sephora.data.service;

import com.sephora.data.model.Store;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreService {
    /**
     * Charge les magasins depuis un fichier CSV.
     *
     * @param filePath Chemin du fichier CSV
     * @return Liste d'objets Store
     */
    public List<Store> loadStoresFromCsv(String filePath) {
        List<Store> stores = new ArrayList<>();
        String csvSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // 1. Skip l'entête : id,nom,adresse,pays,region,surface,date_ouverture,statut,nb_employes
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(csvSplitBy);

                try {
                    // On suit l'ordre exact de ton entête
                    int id = Integer.parseInt(cleanField(data[0]));
                    String nom = cleanField(data[1]);
                    String adresse = cleanField(data[2]);
                    String pays = cleanField(data[3]);
                    String region = cleanField(data[4]);
                    int surface = Integer.parseInt(cleanField(data[5]));
                    String dateOuverture = cleanField(data[6]);
                    String statut = cleanField(data[7]);
                    int nbEmployes = Integer.parseInt(cleanField(data[8]));

                    stores.add(new Store(id, nom, adresse, pays, region,
                            surface, dateOuverture, statut, nbEmployes));
                } catch (Exception e) {
                    System.err.println("Ligne ignorée (erreur de format) : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stores;
    }

    /**
     * Méthode utilitaire pour supprimer les guillemets et les espaces inutiles
     */
    private String cleanField(String field) {
        return (field == null) ? "" : field.replace("\"", "").trim();
    }
    /**
     * Compte le nombre de magasins par pays sans utiliser les Streams.
     */
    public Map<String, Integer> countStoresByCountry(List<Store> stores) {
        // 1. On crée une Map vide pour stocker nos résultats
        Map<String, Integer> paysCompteur = new HashMap<>();

        // 2. On parcourt la liste des magasins un par un
        for (Store store : stores) {
            String pays = store.getPays();

            // 3. On vérifie si le pays est déjà dans la Map
            if (paysCompteur.containsKey(pays)) {
                // Si oui, on récupère la valeur actuelle et on ajoute 1
                int nombreActuel = paysCompteur.get(pays);
                paysCompteur.put(pays, nombreActuel + 1);
            } else {
                // Si non, c'est le premier magasin de ce pays qu'on trouve
                paysCompteur.put(pays, 1);
            }
        }

        return paysCompteur;
    }
    /**
     * charge le magasin le plus ancien.
     * @return objet Store
     */
    public Store findOldestStore(List<Store> stores) {
        // Si la liste est vide, on ne peut rien trouver
        if (stores == null || stores.isEmpty()) {
            return null;
        }

        // Initialiser le "champion" (le plus ancien actuel)
        Store oldest = stores.get(0);

        // Parcourir le reste de la liste
        for (int i = 1; i < stores.size(); i++) {
            Store current = stores.get(i);

            // Comparer avec compareTo()
            if (current.getDateOuverture().compareTo(oldest.getDateOuverture())<0){

                oldest = current;
            }
        }

        return oldest;
    }
    /**
     * charge les magasins après année donnée.
     * @param stores liste des magasins,
     * @param year année
     * @return liste d'objets Store
     */
    public List<Store> filterStoresAfter(List<Store> stores, String year) {
        // initialisation d'une liste des magasins vide
        List<Store> listAfter = new ArrayList<>();
        // on parcourt la liste
        for(Store store : stores){
            // on recupere l'année de magasin
          String storeYear= store.getDateOuverture().substring(0,4);
          // on le compare avec l'année en parametre
         if(storeYear.compareTo(year)>=0){
             // on ajoute donc le magasin dans la liste
             listAfter.add(store);
         }
        }
        return listAfter;
    }
    public void generateStoreReport(String fileName, List<Store> stores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("==========================================");
            writer.println("       RAPPORT DES MAGASINS SEPHORA      ");
            writer.println("==========================================");
            writer.println("Nombre total de magasins : " + stores.size());
            writer.println();

            // 1. Analyse du magasin le plus ancien
            writer.println("--- MAGASIN HISTORIQUE ---");
            Store oldest = findOldestStore(stores);
            if (oldest != null) {
                writer.println("Nom    : " + oldest.getNom());
                writer.println("Pays   : " + oldest.getPays());
                writer.println("Ouvert : " + oldest.getDateOuverture());
            }
            writer.println();

            // 2. Statistiques par pays
            writer.println("--- RÉPARTITION PAR PAYS ---");
            Map<String, Integer> countries = countStoresByCountry(stores);
            for (Map.Entry<String, Integer> entry : countries.entrySet()) {
                writer.println("- " + entry.getKey() + " : " + entry.getValue() + " magasin(s)");
            }

            System.out.println("✅ Rapport généré avec succès dans : " + fileName);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la génération du rapport : " + e.getMessage());
        }
    }
}
