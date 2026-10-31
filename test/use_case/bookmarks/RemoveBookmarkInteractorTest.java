package use_case.bookmarks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import data_access.PetDAOInterface;
import data_access.UserDAOInterface;
import entity.Bookmark;
import entity.Pet;
import entity.user.AdopterUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import use_case.pet_bio.PetBioOutputData;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Unit test class for RemoveBookmarkInteractor.
 * This class tests the functionality of the RemoveBookmarkInteractor using JUnit 5 and Mockito.
 */
public class RemoveBookmarkInteractorTest {

    private UserDAOInterface userDAO;
    private RemoveBookmarkOutputBoundary removeOutputBoundary;
    private RemoveBookmarkInteractor removeBookmarkInteractor;
    private AdopterUser user;
    private ArrayList<Bookmark> allBookmarks;
    private int petID;
    private LocalDateTime time;
    private String username;
    private PetDAOInterface petDAO;
    private Pet pet;

    /**
     * Sets up the necessary objects before each test is run.
     * Initializes the mock objects and sets up the test data.
     */
    @BeforeEach
    public void setUp() {
        petID = 31;
        username = "Test User";
        time = LocalDateTime.of(2022,7,5,9,38);
        allBookmarks = new ArrayList<>();
        allBookmarks.add(new Bookmark("test-user", petID, time));

        userDAO = mock(UserDAOInterface.class);
        user = mock(AdopterUser.class);
        petDAO = mock(PetDAOInterface.class);
        removeOutputBoundary = mock(RemoveBookmarkOutputBoundary.class);
        removeBookmarkInteractor = new RemoveBookmarkInteractor(userDAO, removeOutputBoundary, petDAO);

        pet = new Pet("Owner1", "owner1@example.com", "1234567890", 31, "Cat", 3, "Breed1", "Male", "Active", "Bio1", "Location", true, "Pet1", "img1");

        when(userDAO.get(username)).thenReturn(user);
        when(user.getBookmarks()).thenReturn(allBookmarks);
        when(petDAO.get(petID)).thenReturn(pet);
    }

    /**
     * Tests the success scenario of removing a bookmark.
     * Verifies that the bookmark is correctly removed from the user's list of bookmarks.
     */
    @Test
    public void testExecuteSuccessfullyRemovesBookmark() {
        removeBookmarkInteractor.execute(new BookmarkInputData(username, petID));

        verify(user,times(2)).getBookmarks();
        verify(userDAO).removeBookmark(user.getUsername(), petID);
        verify(removeOutputBoundary).prepareSuccessView(any(BookmarkOutputData.class));
        ArgumentCaptor<Bookmark> bookmarkCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(user).removeBookmark(bookmarkCaptor.capture());

        Bookmark output = bookmarkCaptor.getValue();
        assertEquals(31, output.getPetID());
    }
}
