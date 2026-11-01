package use_case.display_pets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import data_access.PetDAOInterface;
import data_access.UserDAOInterface;
import dto.PetDTO;
import entity.Pet;
import entity.preference.UserPreference;
import entity.user.AdopterUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the {@link DisplayPetsInteractor} use case class.
 * This class verifies the correct behavior of the {@link DisplayPetsInteractor} class's execute method
 * for displaying pets based on user preferences.
 */
public class DisplayPetsInteractorTest {

    @Mock
    private DisplayPetsOutputBoundary displayPetsOutputBoundary;

    @Mock
    private UserDAOInterface userDAO;

    @Mock
    private PetDAOInterface petDAO;

    @InjectMocks
    private DisplayPetsInteractor displayPetsInteractor;

    /**
     * Initializes the mock objects and the {@link DisplayPetsInteractor} instance before each test.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the {@link DisplayPetsInteractor#execute(DisplayPetsInputData)} method.
     * Verifies that the method correctly retrieves pets based on user preferences and
     * passes the appropriate data to the presenter.
     */
    @Test
    public void testExecute() {
        // Setup
        String userName = "testUser";
        UserPreference userPreference = new UserPreference("user1", "Cat", List.of("Breed1", "Breed2"), 1, 5, "Active", "Location", "Male");
        AdopterUser user = mock(AdopterUser.class);
        when(user.getUsername()).thenReturn(userName);
        when(user.getPreferences()).thenReturn(userPreference);

        List<Pet> pets = List.of(
                new Pet("Owner1", "owner1@example.com", "1234567890", 1, "Cat", 3, "Breed1", "Male", "Active", "Bio1", "Location", true, "Pet1", "img1"),
                new Pet("Owner2", "owner2@example.com", "0987654321", 2, "Cat", 2, "Breed2", "Female", "Active", "Bio2", "Location", true, "Pet2", "img2")
        );

        when(userDAO.get(userName)).thenReturn(user);
        when(userDAO.getPreferencePets(userName)).thenReturn(new ArrayList<>(pets));

        ArgumentCaptor<DisplayPetsOutputData> captor = ArgumentCaptor.forClass(DisplayPetsOutputData.class);

        DisplayPetsInputData inputData = new DisplayPetsInputData(userName);
        displayPetsInteractor.execute(inputData);

        verify(displayPetsOutputBoundary).prepareLoggedIn(captor.capture());
        DisplayPetsOutputData capturedOutputData = captor.getValue();

        assertEquals(userName, capturedOutputData.getUsername());
        List<PetDTO> petDtoList = capturedOutputData.getPets();
        assertEquals(2, petDtoList.size());
        assertEquals(1, petDtoList.get(0).getPetID());
        assertEquals(2, petDtoList.get(1).getPetID());
    }
}