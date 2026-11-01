package use_case.signup;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import data_access.UserDAOInterface;
import entity.user.AdopterUser;
import entity.user.UserFactory;

/**
 * The {@code SignupInteractor} class implements the {@code SignupInputBoundary} interface and handles the signup process.
 * <p>
 * It validates the signup data, checks if the user already exists, and creates a new user if the data is valid.
 *
 */
public class SignupInteractor implements SignupInputBoundary {

    final UserDAOInterface userDataAccessObject;
    final SignupOutputBoundary userPresenter;
    final UserFactory adopterUserFactory;

    /**
     * Constructs a new {@code SignupInteractor} with the specified dependencies.
     *
     * @param userSignupDAInterface  the data access object for user operations.
     * @param signupOutputBoundary   the presenter for the signup use case.
     * @param adopterUserFactory     the factory for creating new adopter users.
     */
    public SignupInteractor(UserDAOInterface userSignupDAInterface, SignupOutputBoundary signupOutputBoundary,  UserFactory adopterUserFactory) {
        this.userDataAccessObject = userSignupDAInterface;
        this.userPresenter = signupOutputBoundary;
        this.adopterUserFactory = adopterUserFactory;
    }

    /**
     * Executes the signup process with the given input data.
     * <p>
     * Validates the input data and creates a new user if all validations pass. It handles the following:
     * <ul>
     * <li>Checks if fields are empty.</li>
     * <li>Ensures the username, email, and phone number are unique.</li>
     * <li>Validates that passwords match.</li>
     * <li>Verifies the email and phone number formats.</li>
     * </ul>
     *
     * @param signupInputData the data required to perform the signup operation.
     */
    @Override
    public void execute(SignupInputData signupInputData) {
        Pattern emailRegex = Pattern.compile("^[\\w.%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Pattern phoneRegex = Pattern.compile("^[0-9]{10,15}$");

        if (isEmpty(signupInputData.getUsername(), "Username cannot be empty.") ||
                isEmpty(signupInputData.getPassword(), "Password cannot be empty.") ||
                isEmpty(signupInputData.getRepeatPassword(), "Repeat password cannot be empty.") ||
                isEmpty(signupInputData.getName(), "Your name cannot be empty.") ||
                isEmpty(signupInputData.getEmail(), "Email cannot be empty.") ||
                isEmpty(signupInputData.getPhone(), "Phone cannot be empty.")) {
            return;
        }

        if (isExistingUser(userDataAccessObject.existsByName(signupInputData.getUsername()),
                "Username already exists.") ||
                isExistingUser(userDataAccessObject.existsByEmail(signupInputData.getEmail()),
                        "Email already in use.") ||
                isExistingUser(userDataAccessObject.existsByPhone(signupInputData.getPhone()),
                        "Phone number already in use.")) {
            return;
        }

        if (!emailRegex.matcher(signupInputData.getEmail()).matches()) {
            userPresenter.prepareFailView("Invalid email address.");
            return;
        }

        if (!phoneRegex.matcher(signupInputData.getPhone()).matches()) {
            userPresenter.prepareFailView("Invalid phone number.");
            return;
        }
        if (!signupInputData.getPassword().equals(signupInputData.getRepeatPassword())) {
            userPresenter.prepareFailView("Passwords don't match.");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        AdopterUser user = adopterUserFactory.createAdopter(signupInputData.getUsername(), signupInputData.getPassword(), signupInputData.getName(), signupInputData.getEmail(), signupInputData.getPhone());
        userDataAccessObject.save(user);
        SignupOutputData signupOutputData = new SignupOutputData(user.getUsername(), now.toString(), false);
        userPresenter.prepareSuccessView(signupOutputData);
    }

    /**
     * Checks if a given field is empty and prepares an error view if so.
     *
     * @param field the field to check.
     * @param errorMessage the error message to present if the field is empty.
     * @return {@code true} if the field is empty, {@code false} otherwise.
     */
    private boolean isEmpty(String field, String errorMessage) {
        if (field == null || field.isBlank()) {
            userPresenter.prepareFailView(errorMessage);
            return true;
        }
        return false;
    }

    /**
     * Checks if a given condition indicates the existence of a user and prepares an error view if so.
     *
     * @param exists {@code true} if the user exists, {@code false} otherwise.
     * @param errorMessage the error message to present if the user exists.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    private boolean isExistingUser(boolean exists, String errorMessage){
        if (exists) {
            userPresenter.prepareFailView(errorMessage);
            return true;
        }
        return false;
    }
}
