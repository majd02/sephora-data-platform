package com.sephora.data.service;

import com.sephora.data.model.Material;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MaterialService {

    /**
     * Calcule le nombre de composants pour chaque type de certification (Bio, Vegan, etc.)
     */
    public Map<String, Long> countMaterialsByCertification(List<Material> materials) {
        return materials.stream()
                .collect(Collectors.groupingBy(
                        Material::getCertification,
                        Collectors.counting()
                ));
    }

    /**
     * Récupère les 5 matières les plus présentes dans la liste
     */
    public List<Map.Entry<String, Long>> getTop5Materials(List<Material> materials) {
        return materials.stream()
                .collect(Collectors.groupingBy(
                        Material::getMatiere,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les 5 fournisseurs qui fournissent le plus grand nombre de composants
     */
    public List<Map.Entry<String, Long>> getTop5Suppliers(List<Material> materials) {
        return materials.stream()
                .collect(Collectors.groupingBy(
                        Material::getFournisseur,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
