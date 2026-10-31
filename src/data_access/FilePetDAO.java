package data_access;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import entity.Pet;
import entity.preference.UserPreference;

/**
 * The {@code FilePetDAO} class is responsible for handling the retrieval, parsing, and saving
 * of pet data to a JSON file. It implements the {@link PetDAOInterface} and interacts with the
 * RescueAPI to fetch pet data based on user preferences and availability.
 */
public class FilePetDAO extends RescueAPIPetGet implements PetDAOInterface {
    private File jsonFile;
    private final Map<String, Pet> pets = new HashMap<>();

    /**
     * Constructs a {@code FilePetDAO} instance and initializes it with data from the specified JSON file.
     * An empty file starts an offline store. Call fetchAndStorePets explicitly to import listings.
     *
     * @param jsonPath the path to the JSON file that will be used to store the data.
     * @throws IOException if an I/O error occurs while reading the JSON file or interacting with the API.
     */
    public FilePetDAO(String jsonPath) throws IOException {
        super();
        this.jsonFile = new File(jsonPath);
        if (jsonFile.length() == 0) {
            save();
        } else {
            TypeReference<HashMap<String, Pet>> typeRef = new TypeReference<HashMap<String, Pet>>() {};
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.registerModule(new JavaTimeModule());
            pets.putAll(objectMapper.readValue(jsonFile, typeRef));
        }
    }

    /**
     * Retrieves a {@link Pet} entity by its ID.
     *
     * @param petID the ID of the pet to retrieve.
     * @return the {@link Pet} entity with the specified ID, or {@code null} if no such pet exists.
     */
    @Override
    public Pet get(int petID) {
        return pets.get(String.valueOf(petID));
    }

    /**
     * Saves a {@link Pet} entity to the JSON file.
     *
     * @param pet the {@link Pet} entity to save.
     */
    @Override
    public void save(Pet pet) {
        pets.put(String.valueOf(pet.getPetID()), pet);
        save();
    }

    /**
     * Persists the current state of the pet data to the JSON file.
     */
    private void save(){
        try {
            utils.AtomicJsonFile.write(objectMapper, jsonFile, pets);
        } catch (Exception ex) {
            System.out.print("Failed saving file [" + jsonFile.getName() + "]: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Retrieves pets that match the specified user preferences.
     *
     * @param userPreference the user preferences to filter pets by.
     * @return an {@link ArrayList} of {@link Pet} entities that match the user's preferences.
     */
    // changed it so that it takes username instead of preference
    @Override
    public ArrayList<Pet> getPreferencePets(UserPreference userPreference) {
        ArrayList<Pet> matchingPets = new ArrayList<>();
        List<Pet> availablePets = getAvailablePets();
        for (Pet pet : availablePets) {
            if (matchesPreference(pet, userPreference)) {
                matchingPets.add(pet);
            }
        }
        return matchingPets;
    }

    /**
     * Checks if a {@link Pet} entity matches the specified user preferences.
     *
     * @param pet the {@link Pet} entity to check.
     * @param userPreference the user preferences to match against.
     * @return {@code true} if the pet matches the user preferences, {@code false} otherwise.
     */
    static boolean matchesPreference(Pet pet, UserPreference userPreference) {
        return isMatching(userPreference.getSpecies(), pet.getSpecies()) &&
                isMatching(userPreference.getBreeds(), pet.getBreed()) &&
                isInRange(userPreference.getMinAge(), userPreference.getMaxAge(), pet.getPetAge()) &&
                isMatching(userPreference.getActivityLevel(), pet.getActivityLevel()) &&
                isMatching(userPreference.getLocation(), pet.getLocation()) &&
                isMatching(userPreference.getGender(), pet.getGender()) &&
                pet.isAvailable();
    }

    /**
     * Checks if a specific attribute matches the user's preference.
     *
     * @param preference the user's preference for the attribute.
     * @param attribute the attribute to check.
     * @return {@code true} if the attribute matches the preference, {@code false} otherwise.
     */
    private static boolean isMatching(String preference, String attribute) {
        return preference == null || preference.isEmpty() || Objects.equals(preference, attribute);
    }

    /**
     * Checks if a list of attributes matches the user's preferences.
     *
     * @param preferences the user's preferences for the attributes.
     * @param attribute the attribute to check.
     * @return {@code true} if the attribute matches one of the preferences, {@code false} otherwise.
     */
    private static boolean isMatching(List<String> preferences, String attribute) {
        return preferences == null || preferences.isEmpty() || preferences.contains(attribute);
    }

    /**
     * Checks if a value falls within a specified range.
     *
     * @param min the minimum value of the range.
     * @param max the maximum value of the range.
     * @param value the value to check.
     * @return {@code true} if the value is within the range, {@code false} otherwise.
     */
    private static boolean isInRange(int min, int max, int value) {
        return (min == 0 || value >= min) && (max == 0 || value <= max);
    }


    /**
     * Retrieves all pets that are available.
     *
     * @return an {@link ArrayList} of {@link Pet} entities that are currently available.
     */
	private ArrayList<Pet> getAvailablePets() {
		return pets.values().stream().filter(pet -> pet.isAvailable()).collect(Collectors.toCollection(ArrayList::new));
	}


}
