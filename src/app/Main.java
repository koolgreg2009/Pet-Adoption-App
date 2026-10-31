package app;

import java.awt.CardLayout;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.get_notifis.NotifViewModel;
import interface_adapter.bookmark.BookmarkViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.display_pets.DisplayPetsViewModel;
import interface_adapter.pet_bio.PetBioViewModel;
import interface_adapter.preference.PreferenceViewModel;
import interface_adapter.signup.SignupViewModel;
import utils.DatabaseInitializer;

import view.*;

import static app.NotifViewUseCaseFactory.createNotifView;

/**
 * The Main class serves as the entry point to the Pet Adoption application.
 * It initializes the application's main window, sets up the different views
 * using a CardLayout, and creates instances of the necessary view models
 * and data access objects.
 * <p>
 * The application displays a series of views to the user, including login,
 * signup, pet display, bookmarks, notifications, and user profile.
 * </p>
 */
public class Main {

    /**
     * The main method initializes and launches the Pet Adoption application.
     *
     * @param args optional --refresh-pets imports remote listings before opening the window
     */
    public static void main(String[] args) {

        if (java.awt.GraphicsEnvironment.isHeadless()) {
            System.err.println("Pet Adoption requires a graphical desktop. Run Maven tests for headless checks.");
            return;
        }
        String directory = System.getProperty("petadoption.dataDir", ".");
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Path.of(directory));
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" +
                    java.nio.file.Path.of(directory, "data.db").toAbsolutePath());
            new DatabaseInitializer(conn).initializeSchema();
            UserDAOInterface userDAO = new SQLUserDAO(conn);
            SQLPetDAO petDAO = new SQLPetDAO(conn);
            SQLAPIInfoDao infoDAO = new SQLAPIInfoDao(conn);
            if (java.util.Arrays.asList(args).contains("--refresh-pets")) {
                try {
                    petDAO.fetchAndStorePets();
                    infoDAO.getBreedInfo();
                    infoDAO.getLocation();
                } catch (IOException | RuntimeException e) {
                    System.err.println("Pet import failed. Cached data is still available: " + e.getMessage());
                }
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { conn.close(); } catch (SQLException ignored) { }
            }));
            javax.swing.SwingUtilities.invokeLater(() -> createWindow(userDAO, petDAO, infoDAO));
        } catch (SQLException | IOException e) {
            String message = "Could not open application storage. Check the data directory and permissions.";
            System.err.println(message);
            javax.swing.SwingUtilities.invokeLater(() -> javax.swing.JOptionPane.showMessageDialog(null, message,
                    "Startup failed", javax.swing.JOptionPane.ERROR_MESSAGE));
        }
    }

    private static void createWindow(UserDAOInterface userDAO, PetDAOInterface petDAO, APIInfoInterface infoDAO) {
        JFrame application = new JFrame("Pet Adoption");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setResizable(true);
        application.setExtendedState(JFrame.MAXIMIZED_BOTH);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);

        LoginViewModel loginViewModel = new LoginViewModel();
        DisplayPetsViewModel displayPetsViewModel = new DisplayPetsViewModel();
        LoggedInViewModel loggedInViewModel = new LoggedInViewModel();
        SignupViewModel signupViewModel = new SignupViewModel();
        BookmarkViewModel bookmarkViewModel = new BookmarkViewModel();
        NotifViewModel notifViewModel = new NotifViewModel();
        PreferenceViewModel preferenceViewModel = new PreferenceViewModel();
        PetBioViewModel petBioViewModel = new PetBioViewModel();

        SignupView signupView = SignupUseCaseFactory.create(viewManagerModel, loginViewModel, signupViewModel,
                preferenceViewModel, userDAO, displayPetsViewModel);
        views.add(signupView, signupView.viewName);

        LoginView loginView = LoginUseCaseFactory.create(viewManagerModel, loginViewModel, displayPetsViewModel,
                signupViewModel, userDAO, petDAO);
        views.add(loginView, loginView.viewName);

        DisplayPetsView displayPetsView = DisplayPetsUseCaseFactory.create(viewManagerModel, displayPetsViewModel,
                loggedInViewModel, userDAO, petDAO);
        views.add(displayPetsView, displayPetsView.viewName);

        LoggedInView loggedInView = LoggedInUseCaseFactory.create(viewManagerModel, loggedInViewModel,
                bookmarkViewModel, preferenceViewModel, loginViewModel, notifViewModel,
                userDAO, petDAO, petBioViewModel, displayPetsViewModel);
        views.add(loggedInView, loggedInView.viewName);

        PetDetailView petDetailView = PetDetailUseCaseFactory.create(viewManagerModel, petBioViewModel,
                loggedInViewModel, userDAO, petDAO);
        views.add(petDetailView, petDetailView.viewName);

        BookmarkView bookmarkView = RemoveBookmarkUseCaseFactory.create(bookmarkViewModel, loggedInViewModel,
                preferenceViewModel, loginViewModel, petBioViewModel, viewManagerModel, notifViewModel,
                displayPetsViewModel, userDAO,petDAO);
        views.add(bookmarkView, bookmarkView.viewName);

        NotifView notifView = createNotifView(loggedInViewModel, viewManagerModel, notifViewModel);
        views.add(notifView, notifView.viewName);

        ProfileView profileView = new ProfileView();
        views.add(profileView, profileView.viewName);

        PreferenceView preferenceView = PreferenceUsecaseFactory.create(viewManagerModel, loggedInViewModel,
                preferenceViewModel, userDAO, displayPetsViewModel, petDAO, infoDAO);
        views.add(preferenceView, preferenceView.viewName);

        viewManagerModel.setActiveView(loginView.viewName);
        viewManagerModel.firePropertyChanged();

        application.setSize(1100, 750);
        application.setMinimumSize(new java.awt.Dimension(800, 600));
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}