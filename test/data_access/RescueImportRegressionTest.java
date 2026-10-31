package data_access;

import entity.Pet;
import entity.preference.UserPreference;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RescueImportRegressionTest {
    private static class Store extends RescueAPIPetGet implements PetDAOInterface {
        final Map<Integer, Pet> pets = new HashMap<>();
        String response = "{\"data\":[{\"id\":\"42\",\"attributes\":{\"name\":\"Milo\",\"ageString\":\"6 Months\"},"
                + "\"relationships\":{\"orgs\":{\"data\":[{\"id\":\"1\"}]}}}]}";
        @Override protected String makeAPICall(String endpoint) {
            if (endpoint.contains("/orgs/")) return "{\"data\":[{\"attributes\":{\"name\":\"Shelter\"}}]}";
            return response;
        }
        public void save(Pet pet) { pets.put(pet.getPetID(), pet); }
        public Pet get(int id) { return pets.get(id); }
        public ArrayList<Pet> getPreferencePets(UserPreference preference) { return new ArrayList<>(pets.values()); }
    }
    @Test void missingOptionalFieldsAndRepeatImportsPreserveAdoption() throws Exception {
        Store store = new Store();
        store.fetchAndStorePets();
        assertEquals("N/A", store.get(42).getLocation());
        assertEquals(0, store.get(42).getPetAge());
        store.get(42).markUnavailable();
        store.fetchAndStorePets();
        assertEquals(1, store.pets.size());
        assertFalse(store.get(42).isAvailable());
    }
    @Test void invalidPayloadReportsFailureWithoutChangingCache() {
        Store store = new Store();
        store.response = "{}";
        assertThrows(IOException.class, store::fetchAndStorePets);
        assertTrue(store.pets.isEmpty());
    }
}
