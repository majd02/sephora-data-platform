package com.sephora.data;

import com.sephora.data.client.AkeneoApiService;
import com.sephora.data.model.DeltaOperation;
import com.sephora.data.model.Product;
import com.sephora.data.model.Store;
import com.sephora.data.parser.FileParserFactory;
import com.sephora.data.parser.JsonDeltaParser;
import com.sephora.data.parser.SephoraFileParser;
import com.sephora.data.service.StoreMergeService;
import com.sephora.data.service.StoreService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Définition des chemins
        String storePath = "/data/stores_full.csv";
        String productPath = "/data/products_full.json";
        String materialPath = "/data/materials_full.xml";
        String brandPath = "/data/brands_full.csv";
        String deltaPath = "/data/stores_delta.json";
        String rapportFile = "/data/RapportFile";

        String[] allFiles = {storePath, productPath, materialPath, brandPath};

        StoreService storeService = new StoreService();
        List<Store> mesMagasins = null;

        // --- ORDRE PRÉCÉDENT CONSERVÉ ---

        // ÉTAPE 1 : CHARGEMENT VIA FACTORY
        System.out.println("=== CHARGEMENT VIA FACTORY ===");
        for (String path : allFiles) {
            try {
                SephoraFileParser<?> parser = FileParserFactory.getParser(path);
                List<?> data = parser.parse(path);

                System.out.println("Fichier : " + path + " | Éléments : " + data.size());

                if (path.equals(storePath)) {
                    mesMagasins = (List<Store>) data;
                }
            } catch (Exception e) {
                System.err.println("Erreur sur " + path + " : " + e.getMessage());
            }
        }

        // ÉTAPE 2 : ANALYSE MÉTIER INITIALE (SUR MESMAGASINS)
        if (mesMagasins != null) {
            System.out.println("\n=== ANALYSE MÉTIER (STORES) ===");
            System.out.println("Magasins par pays : " + storeService.countStoresByCountry(mesMagasins));

            Store oldest = storeService.findOldestStore(mesMagasins);
            System.out.println("Le plus ancien : " + oldest.getNom() + " (ouvert le " + oldest.getDateOuverture() + ")");

            String year = "2020";
            int countAfter = storeService.filterStoresAfter(mesMagasins, year).size();
            System.out.println("Ouverts après " + year + " : " + countAfter + " magasins");

            storeService.generateStoreReport(rapportFile, mesMagasins);
        }

        // ÉTAPE 3 : CHARGEMENT DU DELTA ET MERGE
        System.out.println("\n=== PROCESSUS DE MERGE (DELTA) ===");

        // On récupère le delta
        JsonDeltaParser deltaParser = new JsonDeltaParser();
        List<DeltaOperation<Store>> deltas = deltaParser.parseDelta(deltaPath);

        // Calcul des stats pour le résumé
        Map<String, Long> stats = deltas.stream()
                .collect(Collectors.groupingBy(DeltaOperation::getAction, Collectors.counting()));

        // Application du merge
        StoreMergeService mergeService = new StoreMergeService();
        List<Store> silverStores = mergeService.applyDelta(mesMagasins, deltas);

        // ÉTAPE 4 : AFFICHAGE DU RÉSULTAT DU MERGE
        System.out.println("\n=== Résultat du merge ===");
        System.out.println("Stores avant  : " + (mesMagasins != null ? mesMagasins.size() : 0));
        System.out.println("INSERT        : " + stats.getOrDefault("INSERT", 0L));
        System.out.println("UPDATE        : " + stats.getOrDefault("UPDATE", 0L));
        System.out.println("DELETE        : " + stats.getOrDefault("DELETE", 0L));

        long avant = (mesMagasins != null ? mesMagasins.size() : 0);
        long inserts = stats.getOrDefault("INSERT", 0L);
        long deletes = stats.getOrDefault("DELETE", 0L);

        System.out.println("Stores après  : " + silverStores.size() + " (" + avant + " + " + inserts + " - " + deletes + ")");
        System.out.println("\nRésultat Final (Couche Silver) : " + silverStores.size() + " magasins.");
        String mergedPath = "/data/stores_merged.json";
        storeService.exportStoresToJson(mergedPath, silverStores);

        // Akeneo

        AkeneoApiService api = new AkeneoApiService("https://api.akeneo.example.com");

        // --- LE FILET DE SÉCURITÉ COMMENCE ICI ---
        try {
            // 1. Authentification
            String token = api.getBearerToken("ton_id", "ton_secret");

            if (token != null) {
                // 2 & 3. Récupération + Pagination + Accumulation
                List<Product> allProducts = api.fetchAllProducts(token);

                // 4. Sauvegarde
                api.saveProductsToFile(allProducts);

                System.out.println("🚀 Tout s'est déroulé parfaitement !");
            }

        }
        // --- GESTION DES ERREURS (Question 5) ---
        catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            // Ce catch s'active si le serveur renvoie du texte alors qu'on attend du JSON
            System.err.println("❌ Erreur : Le serveur a envoyé des données illisibles (JSON malformé).");
        }
        catch (Exception e) {
            // Ce catch s'active pour tout le reste (Coupure internet, URL mal écrite, etc.)
            System.err.println("⚠️ Une erreur imprévue est survenue : " + e.getMessage());
        }

    }
}