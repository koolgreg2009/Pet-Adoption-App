package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * The CatBreedDAO class is responsible for fetching breed information from TheCatAPI.
 * It implements the CatDAOInterface interface and uses the OkHttpClient library to
 * perform HTTP requests.
 */
public class CatBreedDAO implements CatDAOInterface {
    private OkHttpClient client;
    private static final String API_KEY = System.getenv("THECATAPI_API_KEY");
    private static final String API_URL = "https://api.thecatapi.com/v1/breeds/search";

    public CatBreedDAO(){
        this.client = new OkHttpClient.Builder().callTimeout(15, java.util.concurrent.TimeUnit.SECONDS).build();
    }
    public CatBreedDAO(OkHttpClient client) {
        this.client = client;
    }

    /**
     * Fetches breed information for a given breed name from TheCatAPI.
     *
     * @param breedName The name of the breed to fetch information for.
     * @return A JSON string containing the breed information, or null if an error occurs.
     */
    @Override
    public HashMap<String, Object> getBreedInformation(String breedName) {
        if (breedName == null || breedName.isBlank()) return null;
        okhttp3.HttpUrl url = okhttp3.HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("q", breedName).addQueryParameter("attach_image", "1").build();
        Request.Builder builder = new Request.Builder().url(url);
        if (API_KEY != null && !API_KEY.isBlank()) builder.addHeader("x-api-key", API_KEY);
        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) return null;

            String responseBody = response.body().string();
            return parseJsonResponseToMap(responseBody);

        } catch (IOException | org.json.JSONException e) {
            return null;
        }

    }
    /**
     * Parses the JSON response and extracts the required fields into a HashMap.
     *
     * @param jsonResponse The JSON response as a string.
     * @return A HashMap containing the extracted information.
     */
    private HashMap<String, Object> parseJsonResponseToMap(String jsonResponse) {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        if (jsonArray.isEmpty()) {
            return null;
        }
        JSONObject breed = jsonArray.getJSONObject(0);
        HashMap<String, Object> breedInfo = new HashMap<>();
        breedInfo.put("name", breed.getString("name"));
        breedInfo.put("description", breed.getString("description"));
        breedInfo.put("adaptability", breed.getInt("adaptability"));
        breedInfo.put("affection_level", breed.getInt("affection_level"));
        breedInfo.put("child_friendly", breed.getInt("child_friendly"));
        breedInfo.put("dog_friendly", breed.getInt("dog_friendly"));
        breedInfo.put("energy_level", breed.getInt("energy_level"));
        breedInfo.put("image_url", breed.optJSONObject("image") == null ? "" : breed.getJSONObject("image").optString("url", ""));

        return breedInfo;
    }

}

