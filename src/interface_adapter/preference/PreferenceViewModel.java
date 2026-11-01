package interface_adapter.preference;

import interface_adapter.ViewModel;


import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages the state and behavior of the preference view, including user input and validation.
 * It provides options for user preferences, manages state, handles user input, and notifies
 * listeners of changes in the preference state.
 */
public class PreferenceViewModel extends ViewModel {

    /** The label for the login button. */
    public final String SAVE_BUTTON_LABEL = "Save Preferences";
    public final String CLEAR_BUTTON_LABEL = "Clear Preferences";
    /** Keys for view fields */
    public final String BREED_KEY = "breeds";
    public final String LOCATION_KEY = "locations";

    /** Time delay between popups */
    private static final int DELAY_MS = 1000;
    /** The current state of the login view. */
    private PreferenceState state = new PreferenceState();

    /**
     * Options for pop up menus
     */
    private final List<String> speciesOptions = Arrays.asList("","Cat");
    private final List<String> activityLevelOptions = Arrays.asList("","Not Active", "Slightly Active", "Moderately Active", "Highly Active");
    private final List<String> genderOptions = Arrays.asList("","Male", "Female");
    private Timer delayTimer;
    private Timer interactionTimer;

    /**
     * Constructs a new PreferenceViewModel with default settings.
     */
    public PreferenceViewModel() {
        super("preference");
    }

    /**
     * Sets the state of the preference view model.
     *
     * @param state The new preference state to set.
     */
    public void setState(PreferenceState state) {
        this.state = state;
    }

    /**
     * PropertyChangeSupport to manage property change listeners.
     */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Fires a property change event to notify listeners of changes in the preference state.
     *
     * @param propertyName The name of the property that has changed.
     */
    public void firePropertyChanged(String propertyName) {
        support.firePropertyChange(propertyName, null, this.state);
    }

    /**
     * Fires a property change event with a specific key.
     *
     * @param key The key for the property change event.
     */
    public void fireTimePropertyChanged(String key) {
        support.firePropertyChange(key, null, this.state);
    }

    /**
     * This method is a placeholder for firing property change events without specifying
     * a particular property name. It is intended to be overridden by subclasses to provide
     * specific implementations for notifying listeners of changes. In its current form,
     * this method does not perform any action.
     */
    @Override
    public void firePropertyChanged() {}

    /**
     * Adds a property change listener to be notified of changes in the preference state.
     *
     * @param listener The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Gets the current state of the preference view model.
     *
     * @return The current preference state.
     */
    public PreferenceState getState() {
        return state;
    }

    /**
     * Clears the current state and resets it to default values.
     */
    public void clearState(){
        state = new PreferenceState();
    }

    /**
     * Gets the list of available species options.
     *
     * @return The list of species options.
     */
    public List<String> getSpeciesOptions() {
        return speciesOptions;
    }

    /**
     * Gets the list of available activity level options.
     *
     * @return The list of activity level options.
     */
    public List<String> getActivityLevelOptions() {
        return activityLevelOptions;
    }

    /**
     * Gets the list of available gender options.
     *
     * @return The list of gender options.
     */
    public List<String> getGenderOptions() {
        return genderOptions;
    }

    /**
     * Validates preference fields that do not require DAO interaction, such as age fields.
     *
     * @return True if all fields are valid, false otherwise.
     */
    public boolean validatePreferences() {
        state.setMinAge(state.getMinAge().trim());
        state.setMaxAge(state.getMaxAge().trim());
        state.setMinAgeError("");
        state.setMaxAgeError("");
        boolean isValid = true;
        if (!validateMinAge()) {
            isValid = false;
        }

        if (!validateMaxAge()) {
            isValid = false;
        }

        firePropertyChanged();
        return isValid;
    }

    /**
     * Validates the minimum age preference.
     * If the minimum age is empty, it is set to "0" (no preference).
     *
     * @return True if the minimum age is valid, false otherwise.
     */
    private boolean validateMinAge() {
        if (!state.getMinAge().isEmpty()) {
            try {
                int minAge = Integer.parseInt(state.getMinAge());
                if (minAge < 0) {
                    state.setMinAgeError("Minimum age cannot be negative");
                    return false;
                }
            }catch (NumberFormatException e) {
                state.setMinAgeError("Minimum age must be an integer");
                return false;
            }
        }else{
            state.setMinAgeError("");
            state.setMinAge("0");
        }

        return true;
    }

    /**
     * Validates the maximum age preference.
     * If the maximum age is empty, it is set to "0" (no preference).
     *
     * @return True if the maximum age is valid, false otherwise.
     */
    private boolean validateMaxAge() {
        if (!state.getMaxAge().isEmpty()) {
            try {
                int maxAge = Integer.parseInt(state.getMaxAge());
                if (maxAge < 0) {
                    state.setMaxAgeError("Maximum age cannot be negative");
                    return false;
                } else if (!state.getMinAge().isEmpty()) {
                    int minAge = Integer.parseInt(state.getMinAge());
                    if (maxAge != 0 && maxAge < minAge) {
                        state.setMaxAgeError("Maximum age cannot be less than minimum age");
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                state.setMaxAgeError("Maximum age must be an integer");
                return false;
            }
        } else {
            state.setMaxAgeError("");
            state.setMaxAge("0");
        }
        return true;
    }

    /**
     * Capitalizes the first letter of each word in the input string.
     *
     * @param input The input string to format.
     * @return The formatted string with capitalized first letters.
     */
    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split(" ");

        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalizedWords.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
            capitalizedWords.append(" ");
        }
        return capitalizedWords.toString().trim();
    }

    /**
     * Capitalizes the first letter of each word in the input array of strings.
     *
     * @param input The input array of strings to format.
     * @return A list of formatted strings with capitalized first letters.
     */
    public List<String> capitalizeFirstLetter(String[] input) {
        List<String> newList = new ArrayList<>();
        if (input.length != 1 | !input[0].isEmpty()) {
            for (String word : input) newList.add(!word.isEmpty() ? capitalizeFirstLetter(word.trim()) : word);
        }
        return newList;
    }

    /**
     * Handles user input and updates the preference state accordingly.
     * Initiates dropdown menu conditions based on user input.
     *
     * @param keyChar The character input by the user.
     * @param key     The key indicating which field the user is interacting with.
     */
    public void handleUserInput(char keyChar, String key){
        PreferenceState currentState = getState();
        switch(key){
            case BREED_KEY:
                currentState.setBreed(currentState.getBreed() + keyChar);
                break;
            case LOCATION_KEY:
                currentState.setLocation(currentState.getLocation() + keyChar);
                break;
        }
        setState(currentState);
        createDelay(key);
        userInteracted();
    }

    /**
     * Creates a delay before showing the dropdown menu based on user input.
     * If a timer is already running, it is restarted.
     * After the delay, if interaction is true, triggers a view observer to execute use case.
     *
     * @param key The key indicating which field the user is interacting with.
     */
    private void createDelay(String key) {
        if (delayTimer != null && delayTimer.isRunning()) {
            delayTimer.stop();
        }
        delayTimer = new Timer(DELAY_MS, e -> {
            if (state.isInteraction()) {
                fireTimePropertyChanged(key);
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    /**
     * Sets the interaction status to true after a delay. If a timer is already running,
     * it is restarted.
     */
    private void userInteracted() {
        state.setInteraction(true);

        if (interactionTimer != null && interactionTimer.isRunning()) {
            interactionTimer.stop();
        }

        interactionTimer = new Timer(DELAY_MS, e -> state.setInteraction(false));
        interactionTimer.setRepeats(false);
        interactionTimer.start();
    }
}

