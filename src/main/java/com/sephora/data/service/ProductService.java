package com.sephora.data.service;

import com.sephora.data.model.Product;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService {

    // Nombre de produits par catégorie
    public Map<String, Long> countByCategory(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getCategorie, Collectors.counting()));
    }

    // Nombre de produits vegan par marque (brand_id)
    public Map<Integer, Long> countVeganByBrand(List<Product> products) {
        return products.stream()
                .filter(Product::isVegan)
                .collect(Collectors.groupingBy(Product::getBrandId, Collectors.counting()));
    }

    // Prix moyen par catégorie
    public Map<String, Double> averagePriceByCategory(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategorie,
                        Collectors.averagingDouble(Product::getPrix)
                ));
    }

    // Top 10 des produits les plus chers
    public List<Product> getTop10MostExpensive(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getPrix).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}