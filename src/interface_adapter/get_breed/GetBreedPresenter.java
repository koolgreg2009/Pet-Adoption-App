package interface_adapter.get_breed;

import interface_adapter.pet_bio.PetBioState;
import interface_adapter.pet_bio.PetBioViewModel;
import use_case.get_breed_info.GetBreedOutputBoundary;
import use_case.get_breed_info.GetBreedOutputData;

/**
 * Presenter for formatting and presenting breed information.
 * Implements {@link GetBreedOutputBoundary} to handle the output data from the use case layer and update the view model.
 */
public class GetBreedPresenter implements GetBreedOutputBoundary {
    private final PetBioViewModel petBioViewModel;

    /**
     * Constructs a new {@code GetBreedPresenter} with the specified view model.
     *
     * @param petBioViewModel the {@code PetBioViewModel} used to update the pet bio state and notify changes.
     */
    public GetBreedPresenter(PetBioViewModel petBioViewModel) {
        this.petBioViewModel = petBioViewModel;
    }

    /**
     * Prepares the view with the breed information.
     * Updates the pet bio state with the details from {@code GetBreedOutputData} and notifies the view model.
     *
     * @param getBreedOutputData the data containing breed information to be displayed.
     */
    public void prepareGetBreedView(GetBreedOutputData getBreedOutputData){
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> prepareGetBreedView(getBreedOutputData));
            return;
        }
        PetBioState state = petBioViewModel.getState();
        state.setNotification("Breed Name: " + getBreedOutputData.getBreedName() + "\n" +
                "Description: " + getBreedOutputData.getDescription() + "\n" +
                "Adaptability: " + getBreedOutputData.getAdaptability() + "\n" +
                "Affection Level: " + getBreedOutputData.getAffectionLevel() + "\n" +
                "Child Friendly: " + getBreedOutputData.getChildFriendly() + "\n" +
                "Dog Friendly: " + getBreedOutputData.getDogFriendly() + "\n" +
                "Energy Level: " + getBreedOutputData.getEnergyLevel() + "\n" +
                "Image URL: " + getBreedOutputData.getImgUrl());

        petBioViewModel.fireNotificationChanged();
    }

    /**
     * Prepares the view to display an error message.
     * Updates the pet bio state with the provided error message and notifies the view model.
     *
     * @param errorMessage the error message to be displayed in the view.
     */
    public void prepareFailView(String errorMessage){
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> prepareFailView(errorMessage));
            return;
        }
        PetBioState state = petBioViewModel.getState();
        state.setNotification(errorMessage);
        petBioViewModel.fireNotificationChanged();
    }
}
