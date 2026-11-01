package entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a bookmark made by an adopter user to save pets they are interested in adopting.
 * <p>
 * A bookmark is associated with a specific pet ID and the date and time when the bookmark was created.
 * This class provides methods to access these details and to convert the bookmark to a string representation.
 */

public class Bookmark {
	private final String username;
	private final int petID;
	private final LocalDateTime bookmarkedDate;

	/**
	 * Constructs a {@code Bookmark} object with the specified pet ID and bookmark creation time.
	 *
	 * @param petID the ID of the pet being bookmarked
	 * @param time the date and time when the bookmark was created
	 */
	@JsonCreator()
	public Bookmark(@JsonProperty("username") String username, @JsonProperty("petID") int petID, @JsonProperty("bookmarkedDate") @com.fasterxml.jackson.annotation.JsonAlias("time") LocalDateTime time) {
		this.username = username;
		this.petID = petID;
		this.bookmarkedDate = time;
	}

	/**
	 * Returns the ID of the pet associated with this bookmark.
	 *
	 * @return the pet ID
	 */
	public int getPetID() {
		return petID;
	}

	/**
	 * Returns the date and time when the bookmark was created.
	 *
	 * @return the date and time of bookmark creation
	 */
	public LocalDateTime getBookmarkedDate() {
		return bookmarkedDate;
	}

}