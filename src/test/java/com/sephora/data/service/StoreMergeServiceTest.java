package com.sephora.data.service;

import com.sephora.data.model.DeltaOperation;
import com.sephora.data.model.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class StoreMergeServiceTest {

    private StoreMergeService mergeService;
    private List<Store> initialStores;

    @BeforeEach
    void setUp() {
        mergeService = new StoreMergeService();
        initialStores = new ArrayList<>();
        // On crée un store de base pour les tests d'Update et Delete
        initialStores.add(new Store(1, "Store Initial", "Adresse", "FR", "IDF", 100, "2024-01-01", "OUVERT", 10));
    }

    // --- LES TESTS ISOLÉS ---

    @Test
    void testInsertOperation() {
        // GIVEN: Une opération INSERT pour l'ID 2
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", 2);
        fields.put("nom", "Nouveau Store");
        DeltaOperation<Store> op = new DeltaOperation<>("INSERT", "2026-04-20T10:00:00Z", fields);

        // WHEN: On applique le delta
        List<Store> result = mergeService.applyDelta(initialStores, Collections.singletonList(op));

        // THEN: On doit avoir 2 stores et l'ID 2 doit exister
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getId() == 2));
    }

    @Test
    void testUpdateOperation() {
        // GIVEN: Une opération UPDATE pour l'ID 1 (qui existe déjà)
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", 1);
        fields.put("nom", "Store Modifié");
        DeltaOperation<Store> op = new DeltaOperation<>("UPDATE", "2026-04-20T10:00:00Z", fields);

        // WHEN: On applique le delta
        List<Store> result = mergeService.applyDelta(initialStores, Collections.singletonList(op));

        // THEN: On doit toujours avoir 1 store, mais avec le nouveau nom
        assertEquals(1, result.size());
        assertEquals("Store Modifié", result.get(0).getNom());
    }

    @Test
    void testDeleteOperation() {
        // GIVEN: Une opération DELETE pour l'ID 1
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", 1);
        DeltaOperation<Store> op = new DeltaOperation<>("DELETE", "2026-04-20T10:00:00Z", fields);

        // WHEN: On applique le delta
        List<Store> result = mergeService.applyDelta(initialStores, Collections.singletonList(op));

        // THEN: La liste doit être vide
        assertEquals(0, result.size());
    }
}