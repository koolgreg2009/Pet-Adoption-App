package data_access;

import entity.Bookmark;
import entity.Pet;
import entity.preference.UserPreference;
import entity.user.AdopterUser;
import entity.user.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for use case interactors to communicate with user information stored in SQL tables.
 */
public class SQLUserDAO implements UserDAOInterface{
    private final Connection conn;

    /**
     * Connection object is passed from main
     * @param conn
     * @throws SQLException
     */
    public SQLUserDAO(Connection conn) throws SQLException {
        this.conn = conn;
    }

    /**
     * Saves user. If user already exists update.
     * @param user The user to be saved.
     */
    @Override
    public void save(User user) {
        AdopterUser adopter = (AdopterUser) user;
        try (PreparedStatement stmt = conn.prepareStatement(
                """
                INSERT INTO users (username, password, name, email, phone)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(username) DO UPDATE SET
                    password = excluded.password,
                    name = excluded.name,
                    email = excluded.email,
                    phone = excluded.phone
                """)) {
            stmt.setString(1, adopter.getUsername());
            stmt.setString(2, adopter.getPassword());
            stmt.setString(3, adopter.getName());
            stmt.setString(4, adopter.getEmail());
            stmt.setString(5, adopter.getPhone());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }


    /**
     * Retrieves pets that match the specified user preferences.
     *
     * @param username username
     * Takes username and retrieve matching user preference. Wrapped in userPreference to
     * Query through Pets table for matching preference
     * @return an {@link ArrayList} of {@link Pet} entities that match the user's preferences.
     */

    @Override
    public ArrayList<Pet> getPreferencePets(String username) {
        UserPreference pref = getUserPreference(username);
        List<String> pref_breeds = pref.getBreeds();
        ArrayList<Pet> pets = new ArrayList<>();

        String sql = """
        SELECT *
        FROM pets p
        WHERE p.is_available = 1
          AND (? IS NULL OR ? = '' OR p.species = ?)
          AND (? = 0 OR p.pet_age >= ?)
          AND (? = 0 OR p.pet_age <= ?)
          AND (? IS NULL OR ? = '' OR p.activity_level = ?)
          AND (? IS NULL OR ? = '' OR p.location = ?)
          AND (? IS NULL OR ? = '' OR p.gender = ?);
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;

            // species (1–3)
            pstmt.setString(index++, pref.getSpecies());
            pstmt.setString(index++, pref.getSpecies());
            pstmt.setString(index++, pref.getSpecies());

            // age (4–6)
            pstmt.setInt(index++, pref.getMinAge());
            pstmt.setInt(index++, pref.getMinAge());
            pstmt.setInt(index++, pref.getMaxAge());
            pstmt.setInt(index++, pref.getMaxAge());

            // activity_level (7–9)
            pstmt.setString(index++, pref.getActivityLevel());
            pstmt.setString(index++, pref.getActivityLevel());
            pstmt.setString(index++, pref.getActivityLevel());

            // location (10–12)
            pstmt.setString(index++, pref.getLocation());
            pstmt.setString(index++, pref.getLocation());
            pstmt.setString(index++, pref.getLocation());

            // gender (13–15)
            pstmt.setString(index++, pref.getGender());
            pstmt.setString(index++, pref.getGender());
            pstmt.setString(index++, pref.getGender());


            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (pref_breeds.isEmpty() || pref_breeds.contains(rs.getString("breed"))){
                    Pet pet = new Pet(
                            rs.getString("owner"),
                            rs.getString("email"),
                            rs.getString("phone_num"),
                            rs.getInt("pet_id"),
                            rs.getString("species"),
                            rs.getInt("pet_age"),
                            rs.getString("breed"),
                            rs.getString("gender"),
                            rs.getString("activity_level"),
                            rs.getString("bio"),
                            rs.getString("location"),
                            rs.getInt("is_available") == 1,
                            rs.getString("name"),
                            rs.getString("img_url")
                    );
                    pets.add(pet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to query pets by preference", e);
        }

        return pets;
    }


    /**
     * Checks if username is already in use.
     * @param username
     * @return
     */
    @Override
    public boolean existsByName(String username) {
        String sql = """
                    SELECT 1 FROM users
                    WHERE username = ? LIMIT 1""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if email is already in use.
     * @param email
     * @return
     */
    @Override
    public boolean existsByEmail(String email) {
        String sql = """
                    SELECT 1 FROM users
                    WHERE email = ? LIMIT 1""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if phone number is already in use.
     * @param phone
     * @return
     */
    @Override
    public boolean existsByPhone(String phone) {
        String sql = """
                    SELECT 1 FROM users
                    WHERE phone = ? LIMIT 1""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is actually never used lol. but interface still has it. Can also just do rm <file> to restart database.
     */
    @Override
    public void clearUsers() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM users");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete users", e);
        }
    }

    /**
     * Get method for User
     * @param username
     * @return Java User object
     */
    @Override
    public User get(String username) {
        User user = null;
        String sql = """
                SELECT * 
                FROM users
                WHERE username = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new AdopterUser(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        getUserBookmarks(username),
                        getUserPreference(username));
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return user;
    }

    /**
     * Used by Adopt use case. When Adopt is run, removes the adopted pet from all users' bookmarks.
     * @param petID
     * @return List of usernames that bookmark was removed from.
     */
    @Override
    public List<String> removePetFromAllUserBookmarks(int petID) {
        List<String> usernames = new ArrayList<>();
        String sqlGet = """
                        SELECT username 
                        FROM bookmarks 
                        WHERE pet_id = ?""";
        String sqlDelete =  """
                            DELETE FROM bookmarks
                            WHERE pet_id = ?
                            """;

        try (
                PreparedStatement getStmt = conn.prepareStatement(sqlGet);
                PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete)
        ) {
            // fetch usernames
            getStmt.setInt(1, petID);
            ResultSet rs = getStmt.executeQuery();
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }

            // delete all rows
            deleteStmt.setInt(1, petID);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing pet " + petID + " from bookmarks", e);
        }
        return usernames;
    }

    /**
     * Checks if user has a bookmark.
     * @param username
     * @param petID
     * @return
     */
    @Override
    public boolean userHasBookmark(String username, int petID) {
        String sql = """
                SELECT 1
                FROM bookmarks
                WHERE username = ? AND pet_id = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            pstmt.setObject(2, petID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Get method for user preference.
     * @param username
     * @return
     */
    private UserPreference getUserPreference(String username) {
        UserPreference userPreference = new UserPreference();
        String sql = "SELECT * FROM preferences WHERE username = ?"; // username is primary key
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                userPreference = new UserPreference(
                        rs.getString("username"),
                        rs.getString("species"),
                        getPreferredBreeds(username),
                        rs.getInt("min_age"),
                        rs.getInt("max_age"),
                        rs.getString("activity_level"),
                        rs.getString("location"),
                        rs.getString("gender")
                );
            }
        } catch (SQLException e){
            throw new RuntimeException("Failed to get preference from username" + e);
        }
        return userPreference;
    }

    /**
     * Return preferred breeds of user. Used by getUserPreference method.
     * @param username
     * @return Return preferred breeds of user
     */
    private List<String> getPreferredBreeds(String username) {
        List<String> preferredBreeds = new ArrayList<>();
        String sql = """ 
                SELECT pb.breed
                FROM preferred_breeds pb
                WHERE pb.username = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                preferredBreeds.add(rs.getString("breed"));
            }
        } catch (SQLException e){
            throw new RuntimeException("Failed to get preference from username" + e);
        }
        return preferredBreeds;
    }

    /**
     * Retrieves a user's bookmarks based on username.
     * @param username
     * @return Arraylist of bookmarks
     */
    private ArrayList<Bookmark> getUserBookmarks(String username) {
        ArrayList<Bookmark> bookmarks = new ArrayList<>();

        String sql = """
                SELECT *
                FROM bookmarks
                WHERE username = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                String raw = rs.getString("timestamp");
                // Converting back into LDT format.
                LocalDateTime parsed = LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                bookmarks.add((new Bookmark(
                        rs.getString("username"),
                        rs.getInt("pet_id"),
                        parsed
                )));
            }
        } catch (SQLException e){
            throw new RuntimeException("Failed to get preference from username" + e);
        }
        return bookmarks;
    }

    /**
     * Updates user preference in preferences table. To do so this method also updates the preferred_breeds table.
     * Does 3 actions: First updates preferences, then removes all of user's old breed_preference, then insert all
     * new user preferences. Uses transaction for safety
     * @param username
     * @param prefs
     * @throws SQLException
     */
    @Override
    public void updatePreferences(String username, UserPreference prefs) {
        List<String> breeds = prefs.getBreeds();
        String updatePrefsSQL = """
        INSERT INTO preferences (username, species, min_age, max_age, activity_level, location, gender)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(username) DO
        UPDATE SET
            species = ?,
            min_age = ?,
            max_age = ?,
            activity_level = ?,
            location = ?,
            gender = ?
        WHERE username = ?
    """;

        String deleteBreedsSQL = "DELETE FROM preferred_breeds WHERE username = ?";
        String insertBreedSQL = "INSERT INTO preferred_breeds (username, breed) VALUES (?, ?)";

        try {
            conn.setAutoCommit(false); // begin transaction

            // update preferences
            try (PreparedStatement stmt = conn.prepareStatement(updatePrefsSQL)) {
                int i = 1;

                // INSERT values (1–7)
                stmt.setString(i++, username);
                stmt.setString(i++, prefs.getSpecies());
                stmt.setInt(i++, prefs.getMinAge());
                stmt.setInt(i++, prefs.getMaxAge());
                stmt.setString(i++, prefs.getActivityLevel());
                stmt.setString(i++, prefs.getLocation());
                stmt.setString(i++, prefs.getGender());

                // UPDATE values (8–13)
                stmt.setString(i++, prefs.getSpecies());
                stmt.setInt(i++, prefs.getMinAge());
                stmt.setInt(i++, prefs.getMaxAge());
                stmt.setString(i++, prefs.getActivityLevel());
                stmt.setString(i++, prefs.getLocation());
                stmt.setString(i++, prefs.getGender());

                // WHERE username = ? (14)
                stmt.setString(i++, username);

                stmt.executeUpdate();
            }

            // delete old preferred breeds
            try (PreparedStatement stmt = conn.prepareStatement(deleteBreedsSQL)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }

            // insert new preferred breeds
            try (PreparedStatement stmt = conn.prepareStatement(insertBreedSQL)) {
                for (String breed : breeds) {
                    stmt.setString(1, username);
                    stmt.setString(2, breed);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit(); // success
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Rollback failed", rollbackEx);
            }
            throw new RuntimeException("Failed to update preferences", e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to reset auto-commit", e);
            }
        }
    }

    /**
     * Adds a bookmark to bookmark table.
     * Precondition: Bookmark does not already exist
     * @param username
     * @param petId
     */
    public void addBookmark(String username, int petId, LocalDateTime now) {
        String sql = "INSERT INTO bookmarks (username, pet_id, timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stmt.setString(1, username);
            stmt.setInt(2, petId);
            stmt.setString(3, formatted);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add bookmark", e);
        }
    }

    /**
     * Removes bookmark from bookmark table, does nothing if row doesn't exist.
     * @param username
     * @param petId
     */
    public void removeBookmark(String username, int petId) {
        String sql = "DELETE FROM bookmarks WHERE username = ? AND pet_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, petId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove bookmark", e);
        }
    }

    /**
     * Adds a notification tuple to notifications SQL table based on the notification object
     * @param username
     * @param message
     */

    public void addNotification(String username, String message) {
        String sql = "INSERT INTO notifications (username, message) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not access notifications", e);
        }
    }

    /**
     * Gets all the user's notifications
     * @param username
     * @return List of user's notification objects
     */
    public List<String> getNotifications(String username) {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT notification_id, username, message FROM notifications WHERE username = ? ORDER BY notification_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String message = rs.getString("message");
                notifications.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not access notifications", e);
        }
        return notifications;
    }
}
