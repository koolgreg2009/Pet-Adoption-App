package interface_adapter.adopt;

import utils.SessionManager;
import interface_adapter.display_pets.DisplayPetsViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.adopt.AdoptOutputBoundary;
import use_case.adopt.AdoptOutputData;

/**
 * The {@link AdoptPresenter} class is responsible for presenting the results of the adoption process.
 * It updates the view models with adoption-related information.
 */
public class AdoptPresenter implements AdoptOutputBoundary {
    private final LoggedInViewModel loggedInViewModel;
    private final DisplayPetsViewModel displayPetsViewModel;

    /**
     * Constructs an {@link AdoptPresenter} with the specified view models for managing user notifications
     * and updating pet display information.
     *
     * @param loggedInViewModel the view model used to manage logged-in user notifications
     * @param displayPetsViewModel the view model used to manage pet display updates
     */
    public AdoptPresenter(LoggedInViewModel loggedInViewModel, DisplayPetsViewModel displayPetsViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.displayPetsViewModel = displayPetsViewModel;
    }

    /**
     * Called when a pet is adopted.
     * Updates the logged-in user with a notification about the adoption and the pet owner's contact information.
     * Also triggers a property change event to update the pet display view.
     *
     * @param adoptOutputData the data related to the adopted pet, including ID, owner, email, and phone
     */
    @Override
    public void prepareFailure(String message) {
        loggedInViewModel.setNotification(message, true);
        displayPetsViewModel.firePropertyChanged();
    }

    @Override
    public void prepareAdopt(AdoptOutputData adoptOutputData) {
        loggedInViewModel.setNotification("Thank you " + SessionManager.getInstance().getCurrentUser() + " for adopting "
                + adoptOutputData.getID() + "\n" + "You may contact the owner " + adoptOutputData.getPetOwner()
                + " through: \n" + adoptOutputData.getOwnerEmail() + " and " + adoptOutputData.getOwnerPhone(),
                true);
        displayPetsViewModel.firePropertyChanged();
    }
}
