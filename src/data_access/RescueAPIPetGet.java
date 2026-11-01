package data_access;

import com.fasterxml.jackson.databind.JsonNode;
import entity.Pet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class RescueAPIPetGet extends RescueAPIBase {
    /*

    This class extends the RescueAPIBase and provides methods to retrieve pet to be used by the different PetDAOs
    This should be a standalone class because other classes like FileAPIInfo also uses the abstract but doesnt need to
    implement stuff like save
     */

    public abstract void save(Pet pet);

    /**
     * Fetches and stores pet data from the RescueAPI.
     *
     * @throws IOException if an I/O error occurs during the API interaction.
     */
    public void fetchAndStorePets() throws IOException {
        String response = makeAPICall("/public/animals/search/available/cats?limit=100");
        JsonNode root = objectMapper.readTree(response);
        if (root == null) throw new IOException("RescueGroups returned an empty response.");
        JsonNode data = root.path("data");
        if (!data.isArray()) throw new IOException("RescueGroups returned invalid pet data.");
        JsonNode included = root.path("included");
        Map<String, String> locationMap = parseLocations(included);
        for (JsonNode petNode : data) {
            Pet pet = parsePet(petNode, locationMap);
            if (pet != null && (!(this instanceof PetDAOInterface) ||
                    ((PetDAOInterface) this).get(pet.getPetID()) == null)) {
                save(pet);
            }
        }
    }

    /**
     * Parses location data from the API response.
     *
     * @param included the included node from the API response.
     * @return a map that associates location IDs with city and state strings.
     */
    private Map<String, String> parseLocations(JsonNode included) {
        Map<String, String> locationMap = new HashMap<>();
        if (included.isArray()) {
            for (JsonNode item : included) {
                if ("locations".equals(item.path("type").asText())) {
                    String locationId = item.path("id").asText();
                    String cityState = item.path("attributes").path("citystate").asText();
                    locationMap.put(locationId, cityState);
                }
            }
        }
        return locationMap;
    }

    /**
     * Parses a {@link JsonNode} representing a pet into a {@link Pet} entity.
     *
     * @param petNode the {@link JsonNode} representing the pet.
     * @param locationMap a map of location IDs to city and state strings.
     * @return a {@link Pet} entity, or {@code null} if the parsing fails.
     * @throws IOException if an I/O error occurs during the parsing.
     */
    private Pet parsePet(JsonNode petNode, Map<String, String> locationMap) throws IOException {
        if (!petNode.path("attributes").isObject() || petNode.path("id").asInt(-1) <= 0) {
            throw new IOException("RescueGroups returned a pet without valid attributes or ID.");
        }
        String locationId = petNode.path("relationships").path("locations").path("data").path(0).path("id").asText();
        String location = locationMap.getOrDefault(locationId, "N/A");
        String orgId = petNode.path("relationships").path("orgs").path("data").path(0).path("id").asText();
        if (orgId.isBlank()) return null;
        String orgUrl = "/public/orgs/" + orgId;
        String orgResponseBody = makeAPICall(orgUrl);
        JsonNode orgRoot = objectMapper.readTree(orgResponseBody);
        JsonNode orgData = orgRoot.path("data").path(0).path("attributes");
        String owner = orgData.path("name").asText("N/A");
        String email = orgData.has("email") ? orgData.get("email").asText().replaceAll("\\s+", "") : "N/A";
        String phoneNum = orgData.has("phone") ? orgData.get("phone").asText().replaceAll("\\s+", "") : "N/A";
        int age = petNode.get("attributes").has("ageString") ? parseAgeString(petNode.get("attributes").get("ageString").asText()) : 0;
        String breed = petNode.path("attributes").path("breedPrimary").asText("N/A");
        String desc =  petNode.get("attributes").has("descriptionText") ? removeHTML(petNode.get("attributes").get("descriptionText").asText()) : "N/A";
        String activityLevel = petNode.get("attributes").has("activityLevel")
                ? petNode.get("attributes").get("activityLevel").asText()
                : "N/A";
        String gender = petNode.get("attributes").has("sex") ? petNode.get("attributes").get("sex").asText().replaceAll("\\s+", "") : "N/A";
        String name = "N/A";
        if (petNode.get("attributes").has("name")) {
            name = petNode.get("attributes").get("name").asText();
        }
        String imgUrl = petNode.get("attributes").has("pictureThumbnailUrl") ? petNode.get("attributes").get("pictureThumbnailUrl").asText() : "";
        return new Pet(
                owner,
                email,
                phoneNum,
                petNode.path("id").asInt(),
                "Cat",
                age,
                breed,
                gender,
                activityLevel,
                desc,
                location,
                true,
                name,
                imgUrl
        );
    }

    /**
     * Parses a string representing age into an integer value representing whole years.
     *
     * <p>RI: The format of the ageString must be in "x Years y Months" format.</p>
     *
     * @param ageString the string representing the age of the pet.
     * @return the age of the pet in whole years; kittens younger than one year use zero.
     */
    private int parseAgeString(String ageString) {
        String[] split =  ageString.split(" ");
        try {
            return ageString.toLowerCase().contains("year") ? Integer.parseInt(split[0]) : 0;
        } catch (NumberFormatException e) { return 0; }
    }

    /**
     * Removes common HTML escape characters from a string.
     *
     * @param text the string from which to remove HTML escape characters.
     * @return the cleaned string with HTML escape characters replaced with their corresponding symbols.
     */
    private String removeHTML(String text){
        return text.replace("&nbsp;", " ").replace("&#39;", "'").replace("&amp;", "&").replace("&quot;", "\"");
    }
}
