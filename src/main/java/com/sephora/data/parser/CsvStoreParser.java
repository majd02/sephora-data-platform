package com.sephora.data.parser;

import com.sephora.data.model.Store;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvStoreParser implements SephoraFileParser<Store> {

    @Override
    public List<Store> parse(String filePath) throws Exception {
        List<Store> stores = new ArrayList<>();

        // Utilisation du try-with-resources pour fermer le fichier automatiquement
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // On ignore la première ligne (l'en-tête que tu m'as donnée)
            br.readLine();

            // On parcourt chaque ligne de données
            while ((line = br.readLine()) != null) {
                // split(",") découpe la ligne dès qu'il voit une virgule
                String[] v = line.split(",");

                // Création de l'objet Store
                // On utilise .trim() pour nettoyer les espaces autour des textes
                stores.add(new Store(
                        Integer.parseInt(v[0].trim()), // id
                        v[1].trim(),                   // nom
                        v[2].trim(),                   // adresse
                        v[3].trim(),                   // pays
                        v[4].trim(),                   // region
                        Integer.parseInt(v[5].trim()), // surface
                        v[6].trim(),                   // date_ouverture
                        v[7].trim(),                   // statut
                        Integer.parseInt(v[8].trim())  // nb_employes
                ));
            }
        }
        return stores;
    }
}