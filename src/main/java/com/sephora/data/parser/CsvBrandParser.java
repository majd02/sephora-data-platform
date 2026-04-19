package com.sephora.data.parser;

import com.sephora.data.model.Brand;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvBrandParser implements SephoraFileParser<Brand> {

    @Override
    public List<Brand> parse(String filePath) throws Exception {
        List<Brand> brands = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // On saute l'en-tête : id,nom,groupe,categorie,date_partenariat,pays_origine

            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");

                // Création de l'objet Brand avec les 6 paramètres
                brands.add(new Brand(
                        Integer.parseInt(v[0].trim()), // id
                        v[1].trim(),                   // nom
                        v[2].trim(),                   // groupe
                        v[3].trim(),                   // categorie
                        v[4].trim(),                   // datePartenariat
                        v[5].trim()                    // paysOrigine
                ));
            }
        }
        return brands;
    }
}