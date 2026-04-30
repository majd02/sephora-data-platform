package com.sephora.data.service;

import com.sephora.data.model.DeltaOperation;
import com.sephora.data.model.Store;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class StoreMergeService {

    public List<Store> applyDelta(List<Store> fullData, List<DeltaOperation<Store>> deltas) {
        // Protection contre les listes nulles en entrée
        if (fullData == null) fullData = new ArrayList<>();
        if (deltas == null) return fullData;

        Map<Integer, Store> storeMap = fullData.stream()
                .collect(Collectors.toMap(Store::getId, s -> s, (existing, replacement) -> existing, HashMap::new));

        for (DeltaOperation<Store> op : deltas) {
            Map<String, Object> f = op.getFields();

            // VERIFICATION CRITIQUE : Si fields est null ou si l'ID est absent
            if (f == null || !f.containsKey("id") || f.get("id") == null) {
                System.err.println("⚠️ Opération ignorée : ID manquant dans les champs.");
                continue;
            }

            int id = (int) f.get("id");
            String action = (op.getAction() != null) ? op.getAction().toUpperCase() : "";

            switch (action) {
                case "INSERT":
                    storeMap.put(id, mapToStore(f));
                    break;

                case "UPDATE":
                    if (storeMap.containsKey(id)) {
                        // On modifie directement l'objet qui est dans la Map
                        updateExistingStore(storeMap.get(id), f);
                    } else {
                        System.err.println("UPDATE impossible : ID " + id + " introuvable.");
                    }
                    break;

                case "DELETE":
                    storeMap.remove(id);
                    break;

                default:
                    System.err.println("Action inconnue : " + action);
            }
        }
        return new ArrayList<>(storeMap.values());
    }


    //  Créer un Store à partir de la Map delta
    private Store mapToStore(Map<String, Object> f) {
        return new Store(
                (int) f.get("id"),
                (String) f.getOrDefault("nom", ""),
                (String) f.getOrDefault("adresse", ""),
                (String) f.getOrDefault("pays", ""),
                (String) f.getOrDefault("region", ""),
                (int) f.getOrDefault("surface", 0),
                (String) f.getOrDefault("date_ouverture", ""),
                (String) f.getOrDefault("statut", ""),
                (int) f.getOrDefault("nb_employes", 0)
        );
    }
    // Méthode pour mettre à jour uniquement les champs fournis
    private void updateExistingStore(Store existing, Map<String, Object> f) {
        for (Map.Entry<String, Object> entry : f.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (fieldName.equals("id")) continue;

            try {
                Field field = Store.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(existing, value);

            } catch (NoSuchFieldException e) {
                System.err.println("Champ ignoré (inconnu dans Store.class) : " + fieldName);
            } catch (IllegalAccessException e) {
                System.err.println("Impossible d'accéder au champ : " + fieldName);
            } catch (IllegalArgumentException e) {
                System.err.println("Conflit de type pour le champ '" + fieldName + "' avec la valeur : " + value);
            }
        }
    }
}