package com.sephora.data.service;

import com.sephora.data.model.Brand;
import com.sephora.data.model.Store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandService {
    /**
     * Charge les magasins depuis un fichier CSV.
     *
     * @param filePath Chemin du fichier CSV
     * @return Liste d'objets Brand
     */
    public List<Brand> loadBrandsFromCsv(String filePath) {
        List<Brand> brands   = new ArrayList<>();
        String csvSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // 1. Skip l'entête : id,nom,groupe,categorie,date_partenariat,pays_origine
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(csvSplitBy);

                try {
                    // On suit l'ordre exact de ton entête
                    int id = Integer.parseInt(cleanField(data[0]));
                    String nom = cleanField(data[1]);
                    String groupe = cleanField(data[2]);
                    String categorie = cleanField(data[3]);
                    String datePartenariat = cleanField(data[4]);
                    String paysOrigine = cleanField(data[5]);

                    brands.add(new Brand(id, nom, groupe, categorie, datePartenariat,
                            paysOrigine));
                } catch (Exception e) {
                    System.err.println("Ligne ignorée (erreur de format) : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return brands;
    }
    /**
     * Méthode utilitaire pour supprimer les guillemets et les espaces inutiles
     */
    private String cleanField(String field) {
        return (field == null) ? "" : field.replace("\"", "").trim();
    }
    public Map<String, Integer> countBrandsByCategory(List<Brand> brands) {
        // On prépare notre dictionnaire (Map)
        Map<String, Integer> counts = new HashMap<>();

        // On parcourt chaque marque
        for (Brand b : brands) {
            String category = b.getCategorie(); // ex: "luxe"

            // Si la catégorie existe déjà, on ajoute 1. Sinon, on commence à 1.
            counts.put(category, counts.getOrDefault(category, 0) + 1);
        }

        return counts;
    }
    public Map<String, Integer> countBrandsByGroup(List<Brand> brands) {
        Map<String, Integer> groupCounts = new HashMap<>();

        for (Brand b : brands) {
            String group = b.getGroupe(); // On récupère le groupe (ex: "LVMH")

            // On utilise notre fameuse méthode pour compter
            groupCounts.put(group, groupCounts.getOrDefault(group, 0) + 1);
        }

        return groupCounts;
    }
    public void printTopGroups(Map<String, Integer> groupCounts) {
        // On transforme la Map en une liste d'entrées (paires Groupe/Nombre)
        List<Map.Entry<String, Integer>> list = new ArrayList<>(groupCounts.entrySet());

        // On trie la liste
        // On compare la valeur de 'b' avec celle de 'a' pour avoir un ordre décroissant
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // On affiche le Top 3
        System.out.println("--- Top 3 des Groupes ---");
        for (int i = 0; i < Math.min(3, list.size()); i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + " : " + entry.getValue() + " marques");
        }
    }
}
