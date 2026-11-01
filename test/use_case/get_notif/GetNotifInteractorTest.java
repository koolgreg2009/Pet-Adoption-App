package use_case.get_notif;

import data_access.UserDAOInterface;
import entity.user.AdopterUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link GetNotifInteractor} use case class.
 * This class verifies the behavior of the {@link GetNotifInteractor} class's execute method
 * for retrieving notifications for a user from the {@link UserDAOInterface} and presenting them via the {@link GetNotifOutputBoundary}.
 */
class GetNotifInteractorTest {
    private UserDAOInterface userDAO;
    private GetNotifOutputBoundary presenter;
    private GetNotifInteractor interactor;

    /**
     * Initializes the mock objects and the {@link GetNotifInteractor} instance before each test.
     */
    @BeforeEach
    void setUp() {
        userDAO = Mockito.mock(UserDAOInterface.class);
        presenter = Mockito.mock(GetNotifOutputBoundary.class);
        interactor = new GetNotifInteractor(userDAO, presenter);
    }

    /**
     * Tests the {@link GetNotifInteractor#execute(GetNotifInputData)} method when the user has no notifications.
     * Verifies that the method correctly handles the case where there are no notifications and updates the presenter with an empty list.
     */
    @Test
    void testExecuteNoNotifications() {
        AdopterUser user = new AdopterUser("user1", "password", "User One", "email1@example.com", "123");
        when(userDAO.getNotifications("user1")).thenReturn(user.getNotifications());

        GetNotifInputData inputData = new GetNotifInputData("user1");
        interactor.execute(inputData);

        ArgumentCaptor<GetNotifOutputData> argumentCaptor = ArgumentCaptor.forClass(GetNotifOutputData.class);
        verify(presenter).updateNotif(argumentCaptor.capture());

        GetNotifOutputData outputData = argumentCaptor.getValue();
        assertEquals(0, outputData.getNotif().size());
    }

    /**
     * Tests the {@link GetNotifInteractor#execute(GetNotifInputData)} method when the user has notifications.
     * Verifies that the method correctly retrieves and presents the list of notifications for the user.
     */
    @Test
    void testExecuteWithNotifications() {
        AdopterUser user = new AdopterUser("user1", "password", "User One", "email1@example.com", "123");
        user.addNotif("Notification 1");
        user.addNotif("Notification 2");

        when(userDAO.getNotifications("user1")).thenReturn(user.getNotifications());

        GetNotifInputData inputData = new GetNotifInputData("user1");
        interactor.execute(inputData);

        ArgumentCaptor<GetNotifOutputData> argumentCaptor = ArgumentCaptor.forClass(GetNotifOutputData.class);
        verify(presenter).updateNotif(argumentCaptor.capture());

        GetNotifOutputData outputData = argumentCaptor.getValue();
        List<String> notifications = outputData.getNotif();

        assertEquals(2, notifications.size());
        assertEquals("Notification 1", notifications.get(0));
        assertEquals("Notification 2", notifications.get(1));
    }
}
