package use_case.adopt;

/**
 * Interface for handling the output of adopting the pet.
 */
public interface AdoptOutputBoundary {

    /**
     * Shows a message when a pet has been adopted
     * @param adoptOutputData the output data containing information about the pet
     */
    default void prepareFailure(String message) { }

    void prepareAdopt(AdoptOutputData adoptOutputData);
}
