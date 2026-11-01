package data_access;

import entity.Pet;
import entity.preference.UserPreference;
import entity.user.AdopterUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import use_case.adopt.Adopt;
import use_case.adopt.AdoptInputData;
import use_case.adopt.AdoptOutputBoundary;
import utils.DatabaseInitializer;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersistenceRegressionTest {
    @TempDir Path directory;
    private final LocalDateTime time = LocalDateTime.of(2026, 10, 30, 12, 0);

    private Pet pet() {
        return new Pet("Shelter", "shelter@example.org", "1234567890", 42, "Cat", 3,
                "Siamese", "Female", "High", "Friendly", "Toronto, ON", true, "Milo", "");
    }

    private void populate(UserDAOInterface users, PetDAOInterface pets) {
        users.save(new AdopterUser("alice", "password", "Alice", "alice@example.org", "1234567890"));
        users.updatePreferences("alice", new UserPreference("alice", "Cat", List.of("Siamese"),
                1, 5, "High", "Toronto, ON", "Female"));
        pets.save(pet());
        users.addBookmark("alice", 42, time);
    }

    private void assertReopened(UserDAOInterface users, PetDAOInterface pets) {
        assertTrue(users.userHasBookmark("alice", 42));
        AdopterUser user = (AdopterUser) users.get("alice");
        assertEquals(time, user.getBookmarks().get(0).getBookmarkedDate());
        assertEquals(List.of("Siamese"), user.getPreferences().getBreeds());
        assertEquals(1, pets.getPreferencePets(user.getPreferences()).size());
        assertEquals(List.of("saved"), users.getNotifications("alice"));
    }

    @Test void jsonUpdatesSurviveReopenAndAdoption() throws Exception {
        String userPath = directory.resolve("users.json").toString();
        String petPath = directory.resolve("pets.json").toString();
        FileUserDAO users = new FileUserDAO(userPath);
        FilePetDAO pets = new FilePetDAO(petPath);
        populate(users, pets);
        users.addNotification("alice", "saved");
        users = new FileUserDAO(userPath);
        pets = new FilePetDAO(petPath);
        assertReopened(users, pets);
        new Adopt(pets, mock(AdoptOutputBoundary.class), users).execute(new AdoptInputData(42));
        users = new FileUserDAO(userPath);
        pets = new FilePetDAO(petPath);
        assertFalse(pets.get(42).isAvailable());
        assertFalse(users.userHasBookmark("alice", 42));
        assertEquals(List.of("saved", "Milo has found a home."), users.getNotifications("alice"));
        assertTrue(pets.getPreferencePets(new UserPreference()).isEmpty());
    }

    @Test void sqliteUpdatesSurviveClosingConnectionAndAdoption() throws Exception {
        String url = "jdbc:sqlite:" + directory.resolve("data.db");
        try (Connection conn = DriverManager.getConnection(url)) {
            new DatabaseInitializer(conn).initializeSchema();
            SQLUserDAO users = new SQLUserDAO(conn);
            SQLPetDAO pets = new SQLPetDAO(conn);
            assertTrue(new SQLAPIInfoDao(conn).getData("breeds").isEmpty());
            populate(users, pets);
            users.addNotification("alice", "saved");
            assertEquals(1, users.getPreferencePets("alice").size());
            // SQL list validation must require every breed, not just one matching entry.
            conn.createStatement().executeUpdate("INSERT INTO breeds(data) VALUES ('Siamese')");
            SQLAPIInfoDao info = new SQLAPIInfoDao(conn);
            assertFalse(info.exists(List.of("Siamese", "Invalid"), "breeds"));
            assertTrue(info.exists(List.of("Siamese"), "breeds"));
        }
        try (Connection conn = DriverManager.getConnection(url)) {
            SQLUserDAO users = new SQLUserDAO(conn);
            SQLPetDAO pets = new SQLPetDAO(conn);
            assertReopened(users, pets);
            new Adopt(pets, mock(AdoptOutputBoundary.class), users).execute(new AdoptInputData(42));
        }
        try (Connection conn = DriverManager.getConnection(url)) {
            SQLUserDAO users = new SQLUserDAO(conn);
            SQLPetDAO pets = new SQLPetDAO(conn);
            assertFalse(pets.get(42).isAvailable());
            assertFalse(users.userHasBookmark("alice", 42));
            assertEquals(List.of("saved", "Milo has found a home."), users.getNotifications("alice"));
            assertTrue(users.getPreferencePets("alice").isEmpty());
            AdoptOutputBoundary presenter = mock(AdoptOutputBoundary.class);
            new Adopt(pets, presenter, users).execute(new AdoptInputData(42));
            verify(presenter).prepareFailure(anyString());
            assertEquals(2, users.getNotifications("alice").size());
        }
    }

    @Test void failedJsonSerializationPreservesExistingFile() throws Exception {
        Path file = directory.resolve("preserved.json");
        java.nio.file.Files.writeString(file, "{\"existing\":true}");
        com.fasterxml.jackson.databind.ObjectMapper mapper = mock(com.fasterxml.jackson.databind.ObjectMapper.class);
        doThrow(new java.io.IOException("write failed")).when(mapper).writeValue(any(java.io.File.class), any(Object.class));
        assertThrows(java.io.IOException.class, () -> utils.AtomicJsonFile.write(mapper, file.toFile(), new Object()));
        assertEquals("{\"existing\":true}", java.nio.file.Files.readString(file));
    }

    @Test void newSqlUserCanBrowseWithoutPreferences() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            new DatabaseInitializer(conn).initializeSchema();
            SQLUserDAO users = new SQLUserDAO(conn);
            users.save(new AdopterUser("new", "password", "New", "new@example.org", "1234567890"));
            new SQLPetDAO(conn).save(pet());
            assertEquals(1, users.getPreferencePets("new").size());
        }
    }
}
