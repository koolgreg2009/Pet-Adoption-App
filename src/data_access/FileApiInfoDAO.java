package data_access;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The {@code FileApiInfoDAO} class implements the {@link APIInfoInterface} and is responsible for handling
 * the retrieval, parsing, and saving of API data related to cat breeds and locations.
 * It interacts with a JSON file to store and manage the retrieved data.
 */
public class FileApiInfoDAO extends RescueAPIBase implements APIInfoInterface{
    private final Map<String, List<String>> data = new HashMap<>();
    private File jsonFile;

    /**
     * Constructs a {@code FileApiInfoDAO} instance and initializes it with data from the specified JSON file.
     * If the JSON file is empty, it retrieves breed and location data from the API and saves it.
     *
     * @param jsonPath the path to the JSON file that will be used to store the data.
     * @throws IOException if an I/O error occurs while reading the JSON file or interacting with the API.
     */
    public FileApiInfoDAO(String jsonPath) throws IOException {
        super();
        this.jsonFile = new File(jsonPath);
        if (jsonFile.length() == 0) {
            getBreedInfo();
            getLocation();
        }
        TypeReference<HashMap<String, List<String>>> typeRef = new TypeReference<HashMap<String, List<String>>>() {};
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        data.putAll(objectMapper.readValue(jsonFile, typeRef));
    }

    /**
     * Retrieves a list of data associated with the specified key.
     *
     * @param key the key used to identify the data to retrieve.
     * @return a list of strings representing the data associated with the specified key.
     */
    @Override
    public List<String> getData(String key) {
        return data.get(key);
    }
    /**
     * Retreives all breeds in rescuesAPI database
     * @throws IOException
     */
    @Override
    public void getBreedInfo() throws IOException{
        String response = makeAPICall("/public/animals/breeds/search/cats?limit=250");
        JsonNode root = getObjectMapper().readTree(response);
        JsonNode data = root.get("data");
        List<String> breedNames = new ArrayList<>();
        for (JsonNode node : data) {
            String breedName = node.get("attributes").get("name").asText();
            breedNames.add(breedName);
        }
        save("breeds", breedNames);
    }

    /**
     * Retrieves the 250 of cat locations in database
     * @throws IOException
     */
    @Override
    public void getLocation() throws IOException {
        String response = makeAPICall("/public/animals/search/available/cats?limit=250");
        JsonNode root = getObjectMapper().readTree(response);
        List<String> locations = new ArrayList<>();
        JsonNode includedArray = root.path("included");
        if (includedArray.isArray()) {
            for (JsonNode item : includedArray) {
                if ("locations".equals(item.path("type").asText())) {
                    String cityState = item.path("attributes").path("citystate").asText();
                    if (!Objects.equals(cityState, "") && !locations.contains(cityState)){
                        locations.add(cityState);
                    }
                }
            }
        }
        save("locations", locations);
    }

    /**
     * Saves the given data to the JSON file under the specified key.
     *
     * @param key the key used to identify the data to save.
     * @param names a list of strings representing the  names to save.
     */
    public void save(String key, List<String> names){
        data.put(key, names);
        save();
    }

    /**
     * Persists the current state of the data to the JSON file.
     */
    void save() {
        try {
            utils.AtomicJsonFile.write(getObjectMapper(), jsonFile, data);
        } catch (Exception ex) {
            System.out.print("Failed saving file [" + jsonFile.getName() + "]: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Checks if a specific string exists in the data associated with the specified key.
     *
     * @param string the string to check for existence.
     * @param key the key used to identify the data to check against.
     * @return {@code true} if the string exists, {@code false} otherwise.
     */
    @Override
    public boolean exists(String string, String key){
        return data.get(key).contains(string);
    }

    /**
     * Checks if all of the specified strings exist in the data associated with the specified key.
     *
     * @param names a list of strings to check for existence.
     * @param key the key used to identify the data to check against.
     * @return {@code true} if all the strings exist, {@code false} otherwise.
     */
    public boolean exists(List<String> names, String key){
        for (String name : names){
            if(!data.get(key).contains(name)){
                return false;
            }
        }
        return true;
    }

}