package com.sephora.data;

import com.sephora.data.model.Store;
import com.sephora.data.service.StoreService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    @Test
    void shouldCreateStoreWithAllFields() {
        // Préparation et Action
        Store store = new Store(1, "Sephora Paris Champs-Elysees",
                "70 Av. des Champs-Elysees", "FR", "Ile-de-France",
                450, "2005-03-15", "OUVERT", 65);

        // Vérifications (Assertions)
        assertEquals(1, store.getId(), "L'ID doit être 1");
        assertEquals("Sephora Paris Champs-Elysees", store.getNom());
        assertEquals("70 Av. des Champs-Elysees", store.getAdresse());
        assertEquals("FR", store.getPays());
        assertEquals("Ile-de-France", store.getRegion());
        assertEquals(450, store.getSurface());
        assertEquals("2005-03-15", store.getDateOuverture());
        assertEquals("OUVERT", store.getStatut());
        assertEquals(65, store.getNbEmployes());
    }

    @Test
    void testToString() {
        Store store = new Store(1, "Sephora", "Adresse", "FR", "IDF", 100, "2020-01-01", "OUVERT", 10);

        String result = store.toString();

        // On vérifie que la chaîne contient des informations clés
        assertTrue(result.contains("Sephora"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("OUVERT"));
    }
    @Test
    void testFindOldestStore() {
        // 1. ARRANGE : On prépare des données de test
        List<Store> testList = new ArrayList<>();
        testList.add(new Store(1, "Vieux Magasin", "Paris", "FR", "IDF", 100, "2000-01-01", "Ouvert", 10));
        testList.add(new Store(2, "Nouveau Magasin", "Lyon", "FR", "AURA", 200, "2020-05-20", "Ouvert", 5));

        StoreService service = new StoreService();

        // 2. ACT : On appelle la méthode
        Store result = service.findOldestStore(testList);

        // 3. ASSERT : On vérifie que c'est bien le magasin de l'an 2000
        assertEquals("Vieux Magasin", result.getNom());
    }
    @Test
    void testFilterStoresAfter() {
        // 1. ARRANGE : On crée une liste avec des magasins d'années différentes
        List<Store> testList = new ArrayList<>();
        testList.add(new Store(1, "Vieux", "...", "...", "...", 100, "2000-01-01", "Ouvert", 10));
        testList.add(new Store(2, "Moyen", "...", "...", "...", 150, "2015-06-12", "Ouvert", 12));
        testList.add(new Store(3, "Récent", "...", "...", "...", 200, "2022-11-30", "Ouvert", 8));

        StoreService service = new StoreService();

        // 2. ACT : On veut filtrer les magasins ouverts APRÈS 2010
        List<Store> result = service.filterStoresAfter(testList, "2010");

        // 3. ASSERT : On vérifie les résultats
        // On s'attend à avoir 2 magasins (celui de 2015 et celui de 2022)
        assertEquals(2, result.size());

        // On peut aussi vérifier que le magasin de 2000 n'est PAS dedans
        for (Store s : result) {
            assertTrue(s.getDateOuverture().compareTo("2010-12-31") > 0);
        }
    }
}
