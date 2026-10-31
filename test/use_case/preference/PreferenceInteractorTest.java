package use_case.preference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import use_case.preference.PreferenceData;
import use_case.preference.PreferenceInteractor;
import entity.preference.UserPreference;
import interface_adapter.preference.PreferenceKeys;
import entity.user.AdopterUser;
import data_access.APIInfoInterface;
import data_access.UserDAOInterface;

import java.util.List;

import static org.mockito.Mockito.*;

public class PreferenceInteractorTest {

    private UserDAOInterface userDAO;
    private PreferenceOutputBoundary presenter;
    private APIInfoInterface infoDAO;
    private PreferenceInteractor interactor;

    @BeforeEach
    void setUp() {
        userDAO = Mockito.mock(UserDAOInterface.class);
        presenter = Mockito.mock(PreferenceOutputBoundary.class);
        infoDAO = Mockito.mock(APIInfoInterface.class);
        interactor = new PreferenceInteractor(userDAO, presenter, infoDAO);
    }

    @Test
    void testExecuteWithInvalidBreed() {
        // Arrange
        PreferenceData preferenceData = createTestPreferenceData();
        when(infoDAO.exists(preferenceData.getUserPreference().getBreeds(), preferenceData.getKeys().getBreedKey())).thenReturn(false);
        when(infoDAO.exists(preferenceData.getUserPreference().getLocation(), preferenceData.getKeys().getLocationKey())).thenReturn(true);

        // Act
        interactor.execute(preferenceData);

        // Assert
        verify(presenter).prepareBreedFail();
        verify(userDAO, never()).save(any());
        verify(presenter, never()).prepareSuccessView();
    }

    @Test
    void testExecuteWithInvalidLocation() {
        // Arrange
        PreferenceData preferenceData = createTestPreferenceData();
        when(infoDAO.exists(preferenceData.getUserPreference().getBreeds(), preferenceData.getKeys().getBreedKey())).thenReturn(true);
        when(infoDAO.exists(preferenceData.getUserPreference().getLocation(), preferenceData.getKeys().getLocationKey())).thenReturn(false);

        // Act
        interactor.execute(preferenceData);

        // Assert
        verify(presenter).prepareLocationFail();
        verify(userDAO, never()).save(any());
        verify(presenter, never()).prepareSuccessView();
    }

    @Test
    void testExecuteWithValidPreferences() {
        // Arrange
        PreferenceData preferenceData = createTestPreferenceData();
        when(infoDAO.exists(preferenceData.getUserPreference().getBreeds(), preferenceData.getKeys().getBreedKey())).thenReturn(true);
        when(infoDAO.exists(preferenceData.getUserPreference().getLocation(), preferenceData.getKeys().getLocationKey())).thenReturn(true);
        AdopterUser mockUser = Mockito.mock(AdopterUser.class);
        when(userDAO.get(preferenceData.getUsername())).thenReturn(mockUser);

        // Act
        interactor.execute(preferenceData);

        // Assert
        verify(userDAO).updatePreferences(preferenceData.getUsername(), preferenceData.getUserPreference());
        verify(presenter).prepareSuccessView();
    }

    private PreferenceData createTestPreferenceData() {
        // Create and return a PreferenceData instance suitable for testing
        List<String> breeds = List.of("Breed1", "Breed2");
        String location = "Some Location";
        UserPreference userPreference = new UserPreference("user1", "Dog", breeds, 1, 10, "Medium", location, "Male");
        PreferenceKeys keys = new PreferenceKeys("breedKey", "locationKey");
        return new PreferenceData("testUser", userPreference, keys);
    }
}
