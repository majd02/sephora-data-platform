package com.sephora.data.service;

import com.sephora.data.model.Store;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
     * 1. Compte le nombre de magasins par pays
     */
    public Map<String, Long> countStoresByCountry(List<Store> stores) {
        return stores.stream()
                .collect(Collectors.groupingBy(Store::getPays, Collectors.counting()));
    }

    /**
     * 2. Trouve le magasin le plus ancien
     */
    public Store findOldestStore(List<Store> stores) {
        return stores.stream()
                .min(Comparator.comparing(Store::getDateOuverture))
                .orElse(null);
    }

    /**
     * 3. Top 5 des régions par nombre de magasins
     */
    public List<Map.Entry<String, Long>> getTop5Regions(List<Store> stores) {
        return stores.stream()
                .collect(Collectors.groupingBy(Store::getRegion, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * 4. Surface totale par pays
     */
    public Map<String, Integer> getTotalSurfaceByCountry(List<Store> stores) {
        return stores.stream()
                .collect(Collectors.groupingBy(Store::getPays,
                        Collectors.summingInt(Store::getSurface)));
    }

    /**
     * 5. Liste des magasins en RENOVATION triés par surface décroissante
     */
    public List<Store> getRenovatingStoresSortedBySurface(List<Store> stores) {
        return stores.stream()
                .filter(s -> "RENOVATION".equalsIgnoreCase(s.getStatut()))
                .sorted(Comparator.comparingInt(Store::getSurface).reversed())
                .collect(Collectors.toList());
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
            Map<String, Long> countries = countStoresByCountry(stores);
            for (Map.Entry<String, Long> entry : countries.entrySet()) {
                writer.println("- " + entry.getKey() + " : " + entry.getValue() + " magasin(s)");
            }

            System.out.println("Rapport généré avec succès dans : " + fileName);

        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du rapport : " + e.getMessage());
        }
    }
}
