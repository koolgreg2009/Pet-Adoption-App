package interface_adapter.get_breed;

import use_case.get_breed_info.GetBreedInputBoundary;
import use_case.get_breed_info.GetBreedInputData;

/**
 * Controller class for handling requests to retrieve breed information.
 * Delegates the request to the {@link GetBreedInputBoundary} for processing.
 */
public class GetBreedController {
    private final GetBreedInputBoundary getBreedInteractor;

    /**
     * Constructs a new {@code GetBreedController} with the specified input boundary.
     *
     * @param getBreedInteractor the {@code GetBreedInputBoundary} used to process breed retrieval requests.
     */
    public GetBreedController(GetBreedInputBoundary getBreedInteractor) {
        this.getBreedInteractor = getBreedInteractor;
    }

    /**
     * Executes the request to retrieve breed information.
     * Delegates the task to the {@code GetBreedInputBoundary} by providing the breed name as input data.
     *
     * @param breedName the name of the breed to retrieve information for.
     */
    public void execute(String breedName){
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            new javax.swing.SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() {
                    getBreedInteractor.execute(new GetBreedInputData(breedName));
                    return null;
                }
            }.execute();
        } else {
            this.getBreedInteractor.execute(new GetBreedInputData(breedName));
        }
    }
}
