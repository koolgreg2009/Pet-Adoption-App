package data_access;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

/**
 * Abstract class for interacting with the RescueGroups API and managing local JSON file operations.
 */
public class RescueAPIBase {
    protected final String API_KEY = System.getenv("RESCUEGROUPS_API_KEY");
    protected final String BASE_URL = "https://api.rescuegroups.org/v5";
    protected final OkHttpClient client;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a {@link RescueAPIBase} instance with the specified JSON file path.
     * Sets up an HTTP client with custom timeouts.
     *
     * @throws IOException if an I/O error occurs while initializing the file.
     */
    protected RescueAPIBase() {
        try {
            client = new OkHttpClient.Builder()
                    .callTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create client" + e.getMessage());
        }
    }

    /**
     * Makes a GET request to the specified API endpoint and returns the response body as a string.
     *
     * @param endpoint the API endpoint to call.
     * @return the response body as a string.
     * @throws IOException if the API call fails or the response cannot be read.
     */
    protected String makeAPICall(String endpoint) throws IOException {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IOException("Set RESCUEGROUPS_API_KEY to import pets; cached data can be used offline.");
        }
        Request request = new Request.Builder()
                .url(getBaseURL() + endpoint)
                .addHeader("Authorization", API_KEY)
                .addHeader("Content-Type", "application/vnd.api+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String a = response.body().string();
                return a;
            } else {
                throw new IOException("API call failed with HTTP code: " + response.code());
            }
        }
    }

    /**
     * Returns the base URL for API requests.
     *
     * @return the base URL as a string.
     */
    protected String getBaseURL(){
        return BASE_URL;
    }

    /**
     * Returns the {@link ObjectMapper} instance used for JSON processing.
     *
     * @return the {@link ObjectMapper} instance.
     */
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }


}
