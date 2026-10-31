package use_case.get_breed_info;

import data_access.CatDAOInterface;

import java.util.HashMap;

/**
 * The GetBreedInteractor class is responsible for interacting with the CatDAOInterface
 * to fetch breed information and then passing that information to the presenter
 * to prepare the view.
 */
public class GetBreedInteractor implements GetBreedInputBoundary {
    private final CatDAOInterface catDAO;
    private final GetBreedOutputBoundary getBreedPresenter;

    /**
     * Constructs a new GetBreedInteractor with the given data access object and presenter.
     *
     * @param catDAO The data access object used to fetch breed information.
     * @param getBreedPresenter The presenter used to prepare the breed information view.
     */
    public GetBreedInteractor(CatDAOInterface catDAO, GetBreedOutputBoundary getBreedPresenter) {
        this.catDAO = catDAO;
        this.getBreedPresenter = getBreedPresenter;
    }

    /**
     * Executes the use case to fetch breed information using the provided breed input data.
     * The fetched information is then passed to the presenter to prepare the view.
     *
     * @param breedInputData The input data containing the breed name to fetch information for.
     */
    @Override
    public void execute(GetBreedInputData breedInputData) {
        String breed = breedInputData.getBreedName();
        HashMap<String, Object> breedInfo = this.catDAO.getBreedInformation(breed);
        if (breedInfo != null) {
            String breedName = (String) breedInfo.get("name");
            String description = (String) breedInfo.get("description");
            String adaptability = String.valueOf(breedInfo.get("adaptability"));
            String affection_level = String.valueOf(breedInfo.get("affection_level"));
            String child_friendly = String.valueOf(breedInfo.get("child_friendly"));
            String dog_friendly = String.valueOf(breedInfo.get("dog_friendly"));
            String energy_level = String.valueOf(breedInfo.get("energy_level"));
            String img_url = (String) breedInfo.get("image_url");
            GetBreedOutputData outputData = new GetBreedOutputData(breedName, description, adaptability, affection_level, child_friendly, dog_friendly, energy_level, img_url);
            this.getBreedPresenter.prepareGetBreedView(outputData);
        }else{
            this.getBreedPresenter.prepareFailView("Information on " + breed+" could not be loaded. Check your connection and THECATAPI_API_KEY, or try another breed.");
        }
    }
}
