package data_access;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.Pet;
import entity.Bookmark;
import entity.preference.UserPreference;
import entity.user.AdopterUser;
import entity.user.User;

/**
 * The FileUserDAO class provides an implementation for managing user data stored in a JSON file.
 * It supports loading, saving, and modifying user data, including operations for user bookmarks.
 * This class implements the UserDAOInterface to define the behavior for user data access.
 */
public class FileUserDAO implements UserDAOInterface {
	private final File jsonFile;
	private final Map<String, AdopterUser> accounts = new HashMap<String, AdopterUser>();

	/**
	 * Constructs a new FileUserDAO with the specified JSON file path.
	 * Initializes the user data from the file or creates a new file if it is empty.
	 *
	 * @param jsonPath the path to the JSON file that stores user data.
	 * @throws IOException if an I/O error occurs while reading from or writing to the file.
	 */
	public FileUserDAO(String jsonPath) throws IOException {
		jsonFile = new File(jsonPath);
		if (jsonFile.length() == 0) {
			save();
		} else {
			TypeReference<HashMap<String, AdopterUser>> typeRef = new TypeReference<HashMap<String, AdopterUser>>() {
			};
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			accounts.putAll(objectMapper.readValue(jsonFile, typeRef));
		}
	}

	/**
	 * Saves the specified user to the JSON file.
	 * Updates the in-memory user map and writes the changes to the file.
	 *
	 * @param user the specific user to be saved to the JSON file.
	 */
	@Override
	public void save(User user) {
		accounts.put(user.getUsername(), (AdopterUser) user);
		this.save();
	}

	@Override
	public ArrayList<Pet> getPreferencePets(String username) {
		return null;
	}

	/**
	 * Retrieves the user with the specified username.
	 *
	 * @param username the username of the user
	 * @return The user associated with the specified username, or null if not found.
	 */
	@Override
	public User get(String username) {
		return accounts.get(username);
	}

	/**
	 * Saves the current state of user data to the JSON file.
	 * This method persists the changes made to the in-memory user map by writing it to the file specified
	 * during construction.
	 * <p>
	 * If an error occurs during the saving process, an error message is printed, and a {@link RuntimeException}
	 * is thrown.
	 * </p>
	 */
	private void save() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		try {
			utils.AtomicJsonFile.write(objectMapper, jsonFile, accounts);
		} catch (Exception ex) {
			System.out.print("Failed saving file [" + jsonFile.getName() + "]: " + ex.getMessage());
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Checks if a user with the specified identifier exists in the system.
	 *
	 * @param identifier the unique identifier of the user to check.
	 * @return true if a user with the specified identifier exists; false otherwise.
	 */
	@Override
	public boolean existsByName(String identifier) {
		return accounts.containsKey(identifier);
	}

	/**
	 * Checks if a user with the specified email exists in the system.
	 *
	 * @param email the email address to check.
	 * @return true if a user with the specified email exists; false otherwise.
	 */
	@Override
	public boolean existsByEmail(String email) {
		return accounts.values().stream().anyMatch(user -> user.getEmail().equals(email));
	}

	/**
	 * Checks if a user with the specified phone number exists in the system.
	 *
	 * @param phone the phone number to check.
	 * @return true if a user with the specified phone number exists; false otherwise.
	 */
	@Override
	public boolean existsByPhone(String phone) {
		return accounts.values().stream().anyMatch(user -> user.getPhone().equals(phone));
	}

	/**
	 * Clears all user data from the JSON file and the in-memory user map.
	 * <p>
	 * This method truncates the file to remove all existing user data and clears the in-memory user map.
	 * It returns a string listing the names of all users that were removed, with each name on a new line.
	 * </p>
	 * <p>
	 * If an I/O error occurs while truncating the file, a {@link RuntimeException} is thrown.
	 * </p>
	 *
	 * @return A string containing the names of all removed users, each followed by a newline character.
	 */
	@Override
	public void clearUsers() {
        accounts.clear();
        save();
    }

	/**
	 * Removes a pet from all user bookmarks and returns a list of usernames whose bookmarks were updated.
	 *
	 * @param petID the ID of the pet on the bookmark that is being removed.
	 * @return A list of usernames whose bookmarks were updated.
	 */
	@Override
	public List<String> removePetFromAllUserBookmarks(int petID) {
		List<String> usersNotified = new ArrayList<>();
		accounts.entrySet().stream()
				.filter(a -> a.getValue().getBookmarks().stream().anyMatch(l -> l.getPetID() == petID))
				.forEach(entry -> {entry.getValue().getBookmarks().removeIf(b -> b.getPetID() == petID);
				usersNotified.add(entry.getKey());
				});
		save();
		return usersNotified;
	}

	/**
	 * Checks if a user has bookmarked a pet with the specified ID.
	 *
	 * @param username The username of the user to check.
	 * @param petID The ID of the pet to check for in the user's bookmarks.
	 * @return true if the user has bookmarked the pet; false otherwise.
	 */
	@Override
	public boolean userHasBookmark(String username, int petID) {
		AdopterUser user = accounts.get(username);
		if (user != null) {
			return user.getBookmarks().stream().anyMatch(bookmark -> bookmark.getPetID() == petID);
		}
		return false;
	}

	@Override
	public void updatePreferences(String username, UserPreference preference) {
        accounts.get(username).setPreferences(preference);
        save();
	}

	@Override
	public void addBookmark(String username, int petID, LocalDateTime now) {
        if (!userHasBookmark(username, petID)) {
            accounts.get(username).addBookmark(new Bookmark(username, petID, now));
        }
        save();
	}

	@Override
	public void removeBookmark(String username, int petID) {
        accounts.get(username).getBookmarks().removeIf(b -> b.getPetID() == petID);
        save();
	}

	@Override
	public void addNotification(String username, String message) {
        accounts.get(username).addNotif(message);
        save();
	}

	@Override
	public List<String> getNotifications(String username) {
		return new ArrayList<>(accounts.get(username).getNotifications());
	}

}
