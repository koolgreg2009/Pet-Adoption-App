package view;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.*;

import dto.PetDTO;
import utils.SessionManager;
import interface_adapter.ViewManagerModel;
import interface_adapter.adopt.AdoptController;
import interface_adapter.get_notifis.GetNotifController;
import interface_adapter.get_notifis.NotifViewModel;
import interface_adapter.bookmark.AddBookmarkController;
import interface_adapter.bookmark.BookmarkViewModel;
import interface_adapter.bookmark.RemoveBookmarkController;
import interface_adapter.display_bookmark_pets.DisplayBookmarkController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.pet_bio.PetBioController;
import interface_adapter.preference.PreferenceViewModel;

/**
 * The {@code LoggedInView} class represents the main view for a logged-in user.
 * <p>
 * This panel displays user information, provides navigation buttons, and handles interactions
 * with the pet-related features of the application.
 */
public class LoggedInView extends JPanel implements PetActionView, ActionListener, PropertyChangeListener {

    public final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final BookmarkViewModel bookmarkViewModel;
    private final PreferenceViewModel preferenceViewModel;
    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;
    private final NotifViewModel notifViewModel;

    JLabel username;

    private final JButton logOut;
    private final JButton preferences;
    private final JButton bookmark;
    private final JButton notifications;
    private final JPanel petListingPanel;
	private final PetBioController petBioController;
    private final AdoptController adoptController;
    private final DisplayBookmarkController displayBookmarkController;
    private final AddBookmarkController addBookmarkController;
    private final RemoveBookmarkController removeBookmarkController;
    private final GetNotifController getNotifController;


    /**
     * Constructs a new {@code LoggedInView} with the specified controllers and view models.
     * <p>
     * Initializes the view with buttons and panels, and sets up action listeners and property change listeners.
     *
     * @param petBioController the controller for pet bio actions.
     * @param loggedInViewModel the view model for the logged-in user.
     * @param bookmarkViewModel the view model for bookmarks.
     * @param preferenceViewModel the view model for user preferences.
     * @param loginViewModel the view model for login state.
     * @param notifViewModel the view model for notifications.
     * @param viewManagerModel the view manager model for view navigation.
     * @param adoptController the controller for adopting pets.
     * @param addBookmarkController the controller for adding bookmarks.
     * @param displayBookmarkController the controller for displaying bookmarked pets.
     * @param removeBookmarkController the controller for removing bookmarks.
     * @param getNotifController the controller for fetching notifications.
     */
	public LoggedInView(PetBioController petBioController,
                        LoggedInViewModel loggedInViewModel,
                        BookmarkViewModel bookmarkViewModel,
                        PreferenceViewModel preferenceViewModel,
                        LoginViewModel loginViewModel,
                        NotifViewModel notifViewModel,
                        ViewManagerModel viewManagerModel,
                        AdoptController adoptController,
                        AddBookmarkController addBookmarkController,
                        DisplayBookmarkController displayBookmarkController,
                        RemoveBookmarkController removeBookmarkController,
                        GetNotifController getNotifController) {
		this.petBioController = petBioController;
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.bookmarkViewModel = bookmarkViewModel;
        this.preferenceViewModel = preferenceViewModel;
        this.notifViewModel = notifViewModel;
        this.loginViewModel = loginViewModel;
        this.adoptController = adoptController;
        this.addBookmarkController = addBookmarkController;
        this.displayBookmarkController = displayBookmarkController;
        this.removeBookmarkController = removeBookmarkController;
        this.getNotifController = getNotifController;
        this.loggedInViewModel.addPropertyChangeListener(this);
        this.viewManagerModel.addPropertyChangeListener(this);

        JLabel title = new JLabel("Home Page Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel usernameInfo = new JLabel("Currently logged in: " + loggedInViewModel.getLoggedInUser());
        usernameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        username = new JLabel();

        JPanel buttons = new JPanel();
        notifications = new JButton(loggedInViewModel.NOTIF_BUTTON);
        buttons.add(notifications);
        preferences = new JButton(loggedInViewModel.PREFERENCE_BUTTON_LABEL);
        buttons.add(preferences);
        bookmark = new JButton(loggedInViewModel.BOOKMARK_BUTTON_LABEL);
        buttons.add(bookmark);
        logOut = new JButton(loggedInViewModel.LOGOUT_BUTTON_LABEL);
        buttons.add(logOut);
        petListingPanel = new JPanel();
        petListingPanel.setLayout(new GridLayout(0, 3));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(usernameInfo);
        this.add(username);
        this.add(buttons);
        JScrollPane petScrollPanel = new JScrollPane(petListingPanel);
        this.add(petScrollPanel);
        notifications.addActionListener(
                evt -> {
                    getNotifController.execute(loggedInViewModel.getLoggedInUser());
                    viewManagerModel.setActiveView(notifViewModel.getViewName());
                    viewManagerModel.firePropertyChanged();
                }
        );
        preferences.addActionListener(
                evt -> {
                    viewManagerModel.setActiveView(preferenceViewModel.getViewName());
                    viewManagerModel.firePropertyChanged();
                }
        );
        bookmark.addActionListener(
                evt -> {
                    displayBookmarkController.execute(loggedInViewModel.getLoggedInUser());
                    viewManagerModel.setActiveView(bookmarkViewModel.getViewName());
                    viewManagerModel.firePropertyChanged();
                }
        );
        logOut.addActionListener(
                evt -> {
                    SessionManager.getInstance().logout();
                    viewManagerModel.setActiveView(loginViewModel.getViewName());
                    viewManagerModel.firePropertyChanged();
                }
        );
    }

    /**
     * Handles action events triggered by user interaction.
     * <p>
     * Currently, this method prints the action command to the console.
     *
     * @param evt the action event triggered by the user.
     */
    @Override
	public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    /**
     * Displays notifications based on the current state.
     * <p>
     * Shows a success or error message depending on the notification's status.
     */
    private void showNotification() {
        LoggedInState state = loggedInViewModel.getState();
        if (state.isNotificationSuccess()) {
            JOptionPane.showMessageDialog(this, state.getNotificationMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, state.getNotificationMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Responds to property change events.
     * <p>
     * Updates the username display, pet listings, and notifications based on the event.
     *
     * @param evt the property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername());
            List<PetDTO> pets = state.getPets();
            petListingPanel.removeAll();
            if (pets.isEmpty()) petListingPanel.add(new JLabel("No available pets match your preferences. Clear your filters, or import listings if this is a new installation."));
            for (PetDTO pet : pets) {
                petListingPanel.add(new PetListingPanel(this, pet, true));
            }
            petListingPanel.revalidate();
            petListingPanel.repaint();
        }
        else if ("notification".equals(evt.getPropertyName())) {
            showNotification();
        }
    }

    /**
     * Adds a pet to the bookmarks.
     *
     * @param petID the ID of the pet to be added.
     */
	@Override
	public void add(int petID) {
        addBookmarkController.execute(petID);
	}

    /**
     * Removes a pet from the bookmarks.
     *
     * @param petID the ID of the pet to be removed.
     */
    @Override
    public void remove(int petID){
        removeBookmarkController.execute(petID);
    }

    /**
     * Displays the details of a pet.
     *
     * @param petID the ID of the pet whose details are to be displayed.
     */
	@Override
	public void goDetail(int petID) {
		petBioController.execute(username.getText(), petID);
	}

    /**
     * Handles the adoption of a pet.
     *
     * @param petID the ID of the pet to be adopted.
     */
	@Override
	public void adopt(int petID) {
		adoptController.execute(petID);
	}
}
