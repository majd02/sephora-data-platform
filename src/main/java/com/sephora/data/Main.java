package com.sephora.data;

import com.sephora.data.model.Store;
import com.sephora.data.parser.FileParserFactory;
import com.sephora.data.parser.SephoraFileParser;
import com.sephora.data.service.StoreService;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // 1. Définition des chemins (on prépare les 3 types)
        String storePath = "src/main/resources/data/stores_full.csv";
        String productPath = "src/main/resources/data/products_full.json";
        String materialPath = "src/main/resources/data/materials_full.xml";
        String brandPath = "src/main/resources/data/brands_full.csv";
        String rapportFile = "src/main/resources/data/RapportFile";

        String[] allFiles = {storePath, productPath, materialPath,brandPath};

        StoreService storeService = new StoreService();
        List<Store> mesMagasins = null;

        System.out.println("=== CHARGEMENT VIA FACTORY ===");

        for (String path : allFiles) {
            try {
                // Utilisation de la Factory pour récupérer l'outil sans savoir lequel c'est
                SephoraFileParser<?> parser = FileParserFactory.getParser(path);
                List<?> data = parser.parse(path);

                System.out.println("Fichier : " + path + " | Éléments : " + data.size());

                // Si on vient de charger les magasins, on les garde pour ton analyse spécifique
                if (path.equals(storePath)) {
                    mesMagasins = (List<Store>) data;
                }

            } catch (Exception e) {
                System.err.println("Erreur sur " + path + " : " + e.getMessage());
            }
        }

        // 2. Reprise de ta logique métier spécifique aux Stores
        if (mesMagasins != null) {
            System.out.println("\n=== ANALYSE MÉTIER (STORES) ===");

            // Magasins par pays
            System.out.println("Magasins par pays : " + storeService.countStoresByCountry(mesMagasins));

            // Magasin le plus ancien
            Store oldest = storeService.findOldestStore(mesMagasins);
            System.out.println("Le plus ancien : " + oldest.getNom() + " (ouvert le " + oldest.getDateOuverture() + ")");

            // Filtrage par année
            String year = "2020";
            int countAfter = storeService.filterStoresAfter(mesMagasins, year).size();
            System.out.println("Ouverts après " + year + " : " + countAfter + " magasins");

            // Génération du rapport
            storeService.generateStoreReport(rapportFile, mesMagasins);
        }
    }
}