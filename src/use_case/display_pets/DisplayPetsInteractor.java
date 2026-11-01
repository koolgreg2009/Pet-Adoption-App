package use_case.display_pets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import data_access.PetDAOInterface;
import data_access.UserDAOInterface;
import dto.PetDTO;
import entity.Pet;
import entity.preference.UserPreference;
import entity.user.AdopterUser;
import entity.user.User;

/**
 * Interactor responsible for handling the logic of displaying pets based on user preferences.
 * <p>
 * The interactor retrieves user preferences and fetches pets that match these preferences.
 * It then converts the pet entities to data transfer objects (DTOs) and passes them to the output boundary.
 * </p>
 */
public class DisplayPetsInteractor implements DisplayPetsInputBoundary {
    final DisplayPetsOutputBoundary loginSuccessPresenter;
    final UserDAOInterface userDAO;
	final PetDAOInterface petDAO;

    /**
     * Constructs a new DisplayPetsInteractor with the specified output boundary, user DAO, and pet DAO.
     *
     * @param displayPetsOutputBoundary the output boundary to handle the display of pets
     * @param userDAO the data access object for user information
     * @param petDAO the data access object for pet information
     */
    public DisplayPetsInteractor(DisplayPetsOutputBoundary displayPetsOutputBoundary, UserDAOInterface userDAO, PetDAOInterface petDAO) {
        this.loginSuccessPresenter = displayPetsOutputBoundary;
        this.userDAO = userDAO;
        this.petDAO = petDAO;
    }

    /**
     * Executes the process of retrieving and displaying pets based on the provided input data.
     *
     * @param loggedInInputData the input data containing the username of the user requesting the display of pets
     */
    // rn this doesnt work. does not display pets
    @Override
    public void execute(DisplayPetsInputData loggedInInputData) {
        String userName = loggedInInputData.getUserName();
        User user = userDAO.get(userName);
        //UserPreference pref = ((AdopterUser)user).getPreferences();
        String username = user.getUsername();
		ArrayList<Pet> pets = userDAO.getPreferencePets(username);
        if (pets == null) pets = petDAO.getPreferencePets(((AdopterUser) user).getPreferences());
		List<PetDTO> petDtoList = pets == null ? new ArrayList<PetDTO>()
				: pets.stream()
						.map(pet -> new PetDTO(pet.getPetID(), pet.getName(), pet.getBreed(), pet.getGender(),
								pet.getSpecies(), pet.getPetAge(), pet.getBio(), pet.getOwner(), pet.getEmail(),
								pet.getPhoneNum(), pet.getActivityLevel(), pet.getLocation(), pet.getImgUrl()))
						.collect(Collectors.toList());
		petDtoList.sort((p1, p2) -> p1.getPetID() - p2.getPetID());
		DisplayPetsOutputData displayPetsOutputData = new DisplayPetsOutputData(user.getUsername(), petDtoList);
        loginSuccessPresenter.prepareLoggedIn(displayPetsOutputData);
    }
}
