package com.sephora.data.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sephora.data.model.Product; // Vérifie bien le nom de ton package model
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class AkeneoApiService {

    // On prépare les outils pour toute la classe
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;

    public AkeneoApiService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Authentification
     */
    public String getBearerToken(String clientId, String secret) throws Exception {

        // --- ÉTAPE 1 : Obtenir le token (Requête POST) ---
        HttpRequest authRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"client_id\": \"" + clientId + "\", \"secret\": \"" + secret + "\"}"))
                .build();

        // On envoie la demande
        HttpResponse<String> response = client.send(authRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 401) {
            System.err.println("Erreur 401 : Tes identifiants (Client ID ou Secret) sont faux ou le token est expiré.");
            return null;
        }
        // --- ÉTAPE 2 : Parser la réponse pour extraire le token ---
        JsonNode responseJson = mapper.readTree(response.body());
        String token = responseJson.get("access_token").asText();

        // --- ÉTAPE 3 : Préparer l'appel suivant (Juste pour vérifier que ça marche) ---
        HttpRequest dataRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/products?page=1&limit=50"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        System.out.println("Token récupéré : " + token);
        return token;
    }

    /**
     * Pagination et Désérialisation
     */
    public List<Product> fetchAllProducts(String token) throws Exception {
        // Liste pour accumuler tous les produits ---
        List<Product> allProducts = new ArrayList<>();

        int page = 1;
        boolean hasMore = true;

        while (hasMore) {
            HttpRequest dataRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/products?page=" + page + "&limit=50"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(dataRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                System.err.println("Erreur 404 : L'adresse de l'API (/api/products) n'a pas été trouvée.");
                return allProducts; // On s'arrête et on renvoie ce qu'on a déjà
            }
            // Désérialiser le JSON en List<Product> ---
            List<Product> pageProducts = mapper.readValue(
                    response.body(),
                    new TypeReference<List<Product>>() {}
            );

            if (pageProducts.isEmpty()) {
                hasMore = false;
            } else {
                // --- QUESTION 3 : Ajouter les produits de la page à la liste globale ---
                allProducts.addAll(pageProducts);

                System.out.println("Page " + page + " : " + pageProducts.size() + " produits ajoutés.");
                page++;
            }
        }

        return allProducts; // On retourne la liste complète à la fin
    }
    /**
     * Écrire le résultat complet dans un fichier JSON
     */
    public void saveProductsToFile(List<Product> products) throws Exception {
        // Définir le chemin du fichier (dans ton dossier resources)
        String filePath = "src/main/resources/data/products_akeneo.json";
        File file = new File(filePath);

        // Créer les dossiers parents si ils n'existent pas
        file.getParentFile().mkdirs();

        // Utiliser Jackson pour transformer la liste en fichier JSON bien formaté
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, products);

        System.out.println("💾 Succès ! " + products.size() + " produits ont été écrits dans " + filePath);
    }
}