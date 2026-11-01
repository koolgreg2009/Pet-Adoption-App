package data_access;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import entity.Bookmark;
import entity.user.AdopterUser;
import entity.user.User;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for FileUserDAO.
 * This class tests the functionality of the FileUserDAO using JUnit 5 and Mockito.
 */
public class FileUserDAOTest {

    private ObjectMapper objectMapper;
    private FileUserDAO fileUserDAO;
    private AdopterUser mockUser1;
    private AdopterUser mockUser2;
    private AdopterUser mockUserWithBookmark;
    private File jsonFile;
    private ArrayList<Bookmark> allBookmarks;
    private int petID2;

    /**
     * Sets up the necessary objects before each test is run.
     * Initializes the mock objects, test data, and test file.
     *
     * @throws IOException if an I/O error occurs during setup.
     */
    @BeforeEach
    public void setUp() throws IOException {
        int petID1 = 77;
        petID2= 13;
        LocalDateTime time1 = LocalDateTime.of(2024, 03, 31, 14, 12);
        LocalDateTime time2 = LocalDateTime.of(2024, 7, 12, 8, 01);
        Bookmark bookmark1 = new Bookmark("test-user", petID1, time1);
        Bookmark bookmark2 = new Bookmark("test-user", petID2, time2);
        allBookmarks = new ArrayList<>();
        allBookmarks.add(bookmark1);
        allBookmarks.add(bookmark2);
        jsonFile = new File("test_users.json");
        objectMapper = mock(ObjectMapper.class);
        objectMapper.registerModule(new JavaTimeModule());
        fileUserDAO = new FileUserDAO("test_users.json");
        mockUser1 = new AdopterUser("lollipop123", "142456", "Sam Fries",
                "samfries@live.com", "1234567890");
        mockUser2 = new AdopterUser("bobthebuilder", "6872647ok", "Jimmy Wang",
                "jimw@telus.net", "3451238980");
        mockUserWithBookmark = new AdopterUser("baerizz", "love15in", "Anna Holts",
                "annabanana@yahoo.com", "5205205205", allBookmarks, null);

        objectMapper.writeValue(jsonFile, new ArrayList<>());
    }


    /**
     * Cleans up the test environment after each test is run.
     * Deletes the temporary test file.
     */
    @AfterEach
    public void cleanup() {
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    /**
     * Tests when file length is not zero
     * @throws IOException
     */
    @Test
    public void testConstructorLoadsExistingData() throws IOException {
        // Arrange
        File existingFile = new File("existingFile.json");
        if (!existingFile.exists()) {
            existingFile.createNewFile();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Simulate existing data in the file
        HashMap<String, AdopterUser> mockData = new HashMap<>();
        AdopterUser mockUser = new AdopterUser("existingUser", "password", "John Doe", "john@example.com", "1234567890");
        mockData.put(mockUser.getUsername(), mockUser);
        objectMapper.writeValue(existingFile, mockData);

        // Act
        FileUserDAO fileUserDAO = new FileUserDAO("existingFile.json");

        // Assert
        assertTrue(fileUserDAO.existsByName("existingUser"));

        // Cleanup
        existingFile.delete();
    }

    /**
     * Tests the saving and retrieval of a user.
     * Verifies that the user is correctly saved and retrieved from the DAO.
     *
     * @throws IOException if an I/O error occurs during the test.
     */
    @Test
    public void testSaveAndRetrieveUser() throws IOException {
        fileUserDAO.save(mockUser1);
        User retreivedUser = fileUserDAO.get("lollipop123");
        assertNotNull(retreivedUser);
        assertEquals("lollipop123", retreivedUser.getUsername());
    }

    /**
     * Tests the existence check for a user.
     * Verifies that the DAO correctly identifies whether a user exists.
     */
    @Test
    public void testUserExists() {
        fileUserDAO.save(mockUser2);
        assertTrue(fileUserDAO.existsByName("bobthebuilder"));
        assertFalse(fileUserDAO.existsByName("hannah"));
    }

//    /**
//     * Tests the clearing of all users.
//     * Verifies that users are correctly cleared from the DAO and that no users remain.
//     *
//     * @throws IOException if an I/O error occurs during the test.
//     */
//    @Test
//    public void testClearUsers() throws IOException {
//        fileUserDAO.save(mockUser1);
//        fileUserDAO.save(mockUser2);
//        String clearedUsers = fileUserDAO.clearUsers();
//        assertTrue(clearedUsers.contains("Sam Fries"));
//        assertTrue(clearedUsers.contains("Jimmy Wang"));
//        assertNull(fileUserDAO.get("lollipop123"));
//        assertNull(fileUserDAO.get("bobthebuilder"));
//    }

    /**
     * Tests the removal of a pet from all user bookmarks.
     * Verifies that the pet is removed from all users' bookmarks and that the correct users are notified.
     */
    @Test
    public void testRemovePetFromAllUserBookmarks() {
        fileUserDAO.save(mockUserWithBookmark);
        List<String> notifiedUsers = fileUserDAO.removePetFromAllUserBookmarks(petID2);
        assertEquals(1, notifiedUsers.size());
        assertEquals("baerizz", notifiedUsers.get(0));
        assertFalse(mockUserWithBookmark.getBookmarks().isEmpty());
    }

    /**
     * Tests the check for a user's bookmark.
     * Verifies that the DAO correctly identifies whether a user has a specific bookmark.
     */
    @Test
    public void testUserHasBookmark() {
        fileUserDAO.save(mockUserWithBookmark);
        assertTrue(fileUserDAO.userHasBookmark("baerizz", 77));
        assertTrue(fileUserDAO.userHasBookmark("baerizz", 13));
        assertFalse(fileUserDAO.userHasBookmark("baerizz", 5));
    }

    /**
     * Checks existsByEmail()
     */
    @Test
    public void testExistsByEmail() {
        // Arrange
        AdopterUser user1 = new AdopterUser("user1", "password1", "John Doe", "john@example.com", "1234567890");
        AdopterUser user2 = new AdopterUser("user2", "password2", "Jane Doe", "jane@example.com", "0987654321");
        fileUserDAO.save(user1);
        fileUserDAO.save(user2);

        // Act & Assert
        assertTrue(fileUserDAO.existsByEmail("john@example.com"));
        assertTrue(fileUserDAO.existsByEmail("jane@example.com"));
        assertFalse(fileUserDAO.existsByEmail("notexisting@example.com"));
    }

    /**
     * Checks existsByPhone()
     */
    @Test
    public void testExistsByPhone() {
        // Arrange
        AdopterUser user1 = new AdopterUser("user1", "password1", "John Doe", "john@example.com", "1234567890");
        AdopterUser user2 = new AdopterUser("user2", "password2", "Jane Doe", "jane@example.com", "0987654321");
        fileUserDAO.save(user1);
        fileUserDAO.save(user2);

        // Act & Assert
        assertTrue(fileUserDAO.existsByPhone("1234567890"));
        assertTrue(fileUserDAO.existsByPhone("0987654321"));
        assertFalse(fileUserDAO.existsByPhone("0000000000"));
    }
}