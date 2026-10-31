package use_case.bookmarks;

import dto.BookmarkDTO;
import entity.Bookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BookmarkOutputData} class.
 * <p>
 * This test class verifies the functionality of the {@link BookmarkOutputData}
 * methods {@link BookmarkOutputData#getAllBookmarks()} and {@link BookmarkOutputData#getBookmark()}.
 * </p>
 */
class BookmarkOutputDataTest {

    private BookmarkOutputData testInput;
    private Bookmark bookmark1;
    private Bookmark bookmark2;
    private Bookmark removedBookmark;
    private List<Bookmark> allBookmarks;

    /**
     * Sets up the test environment before each test method is executed.
     * Initializes sample {@link Bookmark} objects and the {@link BookmarkOutputData} instance
     * used for testing.
     */
    @BeforeEach
    void setup() {
        // Create sample Bookmark objects
        bookmark1 = new Bookmark("test-user", 13, LocalDateTime.of(2021, 8, 13, 16, 30));
        bookmark2 = new Bookmark("test-user", 45, LocalDateTime.of(2022, 3, 7, 10, 15));
        removedBookmark = new Bookmark("test-user", 167, LocalDateTime.of(2023, 12, 30, 8, 46));

        // Initialize the list of all bookmarks
        allBookmarks = new ArrayList<>();
        allBookmarks.add(bookmark1);
        allBookmarks.add(bookmark2);


        // Create a BookmarkOutputData object
        testInput = new BookmarkOutputData(allBookmarks, removedBookmark, null, "");
    }

    /**
     * Tests the {@link BookmarkOutputData#getAllBookmarks()} method.
     * <p>
     * Verifies that the method returns the list of all bookmarks with correct size and contents.
     * </p>
     */
    @Test
    void getAllBookmarks() {
        List<Bookmark> result = testInput.getAllBookmarks();
        assertNotNull(result, "The list of all bookmarks should not be null.");
        assertEquals(2, result.size(), "The size of the list should be 2.");
        assertEquals(bookmark1, result.get(0), "The first bookmark should be bookmark1.");
        assertEquals(bookmark2, result.get(1),"The second bookmark should be bookmark2.");
    }

    /**
     * Tests the {@link BookmarkOutputData#getBookmark()} method.
     * <p>
     * Verifies that the method returns the removed bookmark with correct pet ID and date.
     * </p>
     */
    @Test
    void getBookmark() {
        Bookmark result = testInput.getBookmark();
        assertNotNull(result, "The bookmark should not be null.");
        assertEquals(167, result.getPetID(), "The pet ID of the removed bookmark should be 789.");
        assertEquals(LocalDateTime.of(2023,12,30,8,46),
                result.getBookmarkedDate(), "The bookmarked date and time of the removed " +
                        "bookmark should be 2023-12-30 8:46.");
        assertEquals(removedBookmark, result, "The removed bookmark retrieved should match removedBookmark");
    }
}