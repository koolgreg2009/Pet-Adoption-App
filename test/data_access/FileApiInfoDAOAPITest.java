package data_access;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for testing FileApiInfoDAO with a focus on verifying the structure of the data.
 */
class FileApiInfoDAOAPITest {

    private FileApiInfoDAO fileApiInfoDAO;
    private File tempFile;

    /**
     * Sets up the test environment before each test.
     * Creates a temporary file and initializes the FileApiInfoDAO instance.
     *
     * @throws IOException if an I/O error occurs during setup.
     */
    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("pet-api-info-", ".json");
        fileApiInfoDAO = new FileApiInfoDAO(tempFile.getAbsolutePath()) {
            @Override
            protected String makeAPICall(String endpoint) {
                if (endpoint.contains("breeds")) {
                    return "{\"data\":[{\"attributes\":{\"name\":\"Siamese\"}}]}";
                }
                return "{\"included\":[{\"type\":\"locations\",\"attributes\":{\"citystate\":\"Toronto, ON\"}}]}";
            }
        };
    }

    /**
     * Cleans up the test environment after each test.
     * Deletes the temporary file used during the test.
     */
    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    /**
     * Tests that the structure of the data returned by the API is correctly stored in the data.json file.
     * Verifies that the 'locations' and 'breeds' lists are present and have a reasonable structure.
     *
     * @throws IOException if an I/O error occurs during the test.
     */
    @Test
    void testAPIDataStructure() throws IOException {
        // Check that the data is retrieved and structured correctly
        List<String> locations = fileApiInfoDAO.getData("locations");
        List<String> breeds = fileApiInfoDAO.getData("breeds");

        // Assert that the lists are not null
        assertNotNull(locations, "Locations list should not be null");
        assertNotNull(breeds, "Breeds list should not be null");

        // Assert that the lists contain some elements (i.e., they are not empty)
        assertFalse(locations.isEmpty(), "Locations list should not be empty");
        assertFalse(breeds.isEmpty(), "Breeds list should not be empty");

        // Define the regex pattern for location format (e.g., "City, State")
        Pattern locationPattern = Pattern.compile("^.+,.+$");

        // Verify that each location matches the expected format
        for (String location : locations) {
            assertNotNull(location, "Each location should be a non-null string");
            assertFalse(location.trim().isEmpty(), "Each location should not be an empty string");
            assertTrue(locationPattern.matcher(location).matches(), "Location should match the pattern 'City, State'");
        }

        // Optionally, verify that the lists contain expected types of data (e.g., strings for locations and breeds)
        for (String breed : breeds) {
            assertNotNull(breed, "Each breed should be a non-null string");
            assertFalse(breed.trim().isEmpty(), "Each breed should not be an empty string");
        }
    }
}
