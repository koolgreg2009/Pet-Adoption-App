package use_case.adopt;

import data_access.PetDAOInterface;
import data_access.UserDAOInterface;
import entity.Pet;
import entity.user.AdopterUser;
import entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link Adopt} use case class.
 * This class verifies the correct behavior of the {@link Adopt} class's execute method for handling pet adoption scenarios.
 */
class AdoptTest {
    private PetDAOInterface petDAO;
    private UserDAOInterface userDAO;
    private AdoptOutputBoundary presenter;
    private Adopt interactor;

    /**
     * Sets up the test environment before each test method is executed.
     * Mocks dependencies for {@link Adopt} and initializes the {@link Adopt} instance.
     */
    @BeforeEach
    void setUp() {
        petDAO = Mockito.mock(PetDAOInterface.class);
        userDAO = Mockito.mock(UserDAOInterface.class);
        presenter = Mockito.mock(AdoptOutputBoundary.class);
        interactor = new Adopt(petDAO, presenter, userDAO);
    }

    /**
     * Tests the {@link Adopt#execute(AdoptInputData)} method for a successful adoption scenario.
     * Verifies that the pet is marked as unavailable, saved, and notifications are sent to users.
     * Also checks that the presenter is provided with the correct adoption details.
     */
    @Test
    void testExecuteSuccessfulAdoption() {
        Pet pet = Mockito.mock(Pet.class);
        when(petDAO.get(1)).thenReturn(pet);
        when(pet.isAvailable()).thenReturn(true);
        when(pet.getPetID()).thenReturn(1);
        when(pet.getName()).thenReturn("Fluffy");
        when(pet.getOwner()).thenReturn("John Doe");
        when(pet.getEmail()).thenReturn("johndoe@example.com");
        when(pet.getPhoneNum()).thenReturn("123456789");

        AdopterUser user1 = Mockito.mock(AdopterUser.class);
        AdopterUser user2 = Mockito.mock(AdopterUser.class);



        List<String> users = Arrays.asList("user1", "user2");
        when(userDAO.removePetFromAllUserBookmarks(1)).thenReturn(users);

        AdoptInputData inputData = new AdoptInputData(1);
        interactor.execute(inputData);

        verify(pet).markUnavailable();
        verify(petDAO).save(pet);

        verify(userDAO).addNotification("user1", "Fluffy has found a home.");
        verify(userDAO).addNotification("user2", "Fluffy has found a home.");

        ArgumentCaptor<AdoptOutputData> argumentCaptor = ArgumentCaptor.forClass(AdoptOutputData.class);
        verify(presenter).prepareAdopt(argumentCaptor.capture());

        AdoptOutputData outputData = argumentCaptor.getValue();
        assertEquals("John Doe", outputData.getPetOwner());
        assertEquals("johndoe@example.com", outputData.getOwnerEmail());
        assertEquals("123456789", outputData.getOwnerPhone());
        assertEquals("Fluffy", outputData.getID());
    }
}
