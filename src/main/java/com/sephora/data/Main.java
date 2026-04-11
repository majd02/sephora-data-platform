package com.sephora.data;

import com.sephora.data.model.Store;
import com.sephora.data.service.StoreService;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        String path = "src/main/resources/data/stores_full.csv";
        String RapportFile = "src/main/resources/data/RapportFile";
        StoreService storeService  = new StoreService();
        List<Store> mesMagasins = storeService.loadStoresFromCsv(path);
        System.out.println("Nombre de magasins chargés : " + mesMagasins.size());
        System.out.println("=== Magasins par pays ===");
        System.out.println(storeService.countStoresByCountry(mesMagasins).toString());
        System.out.println("=== Magasin le plus ancien  ===");
        Store oldesStore = storeService.findOldestStore(mesMagasins);
        System.out.println(oldesStore.getNom()+"( ouvert le " + oldesStore.getDateOuverture() + ")");
        String year = "2020";
        System.out.println("=== Magasins ouverts après "+ year +" ===");
        System.out.println(storeService.filterStoresAfter(mesMagasins,year).size()+" magasins trouvés");
        storeService.generateStoreReport(RapportFile,mesMagasins);
    }
}