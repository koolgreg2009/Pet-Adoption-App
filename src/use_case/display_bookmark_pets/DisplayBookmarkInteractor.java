package use_case.display_bookmark_pets;

import data_access.PetDAOInterface;
import data_access.UserDAOInterface;
import dto.PetDTO;
import entity.Bookmark;
import entity.Pet;
import entity.user.AdopterUser;
import dto.BookmarkDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactor responsible for handling the display of bookmarked pets.
 * <p>
 * This class retrieves the list of pets bookmarked by a user, filters out
 * those that are unavailable, and prepares the data for presentation.
 * </p>
 */
public class DisplayBookmarkInteractor implements DisplayBookmarkInputBoundary {
    private final PetDAOInterface filePetDAO;
    private final DisplayBookmarkPetsOutputBoundary displayBookmarkPresenter;
    private final UserDAOInterface userDAO;

    /**
     * Constructs a new DisplayBookmarkInteractor with the specified data access objects
     * and output boundary.
     *
     * @param userDAO the data access object for user-related operations
     * @param filepetDAO the data access object for pet-related operations
     * @param displayBookmarkPresenter the output boundary to handle the presentation of the bookmarked pets
     */
    public DisplayBookmarkInteractor(UserDAOInterface userDAO, PetDAOInterface filepetDAO,
                                     DisplayBookmarkPetsOutputBoundary displayBookmarkPresenter) {
        this.filePetDAO = filepetDAO;
        this.displayBookmarkPresenter = displayBookmarkPresenter;
        this.userDAO = userDAO;
    }

    /**
     * Executes the process of retrieving and displaying the user's bookmarked pets.
     * <p>
     * This method fetches the bookmarked pets for a specified user, filters out pets that
     * are not available, and prepares the data for presentation by converting it into
     * DTOs and passing it to the output boundary.
     * </p>
     *
     * @param displayBookmarkInputData the input data containing the username of the user
     */
    public void execute(DisplayBookmarkInputData displayBookmarkInputData){
        AdopterUser user = (AdopterUser)userDAO.get(displayBookmarkInputData.getUsername());
        List<Bookmark> bookmarks = user.getBookmarks();
        List<Pet> pets = new ArrayList<>();
        List<LocalDateTime> times = new ArrayList<>();
        for(Bookmark bookmark: bookmarks){
            Pet pet = filePetDAO.get(bookmark.getPetID());
            if (pet != null && pet.isAvailable()) {
                pets.add(filePetDAO.get(bookmark.getPetID()));
                times.add(bookmark.getBookmarkedDate());
            }
        }
        List<PetDTO> petDtoList = pets == null ? new ArrayList<>()
                : pets.stream()
                .map(pet -> new PetDTO(pet.getPetID(), pet.getName(), pet.getBreed(), pet.getGender(),
                        pet.getSpecies(), pet.getPetAge(), pet.getBio(), pet.getOwner(), pet.getEmail(),
                        pet.getPhoneNum(), pet.getActivityLevel(), pet.getLocation(), pet.getImgUrl()))
                .collect(Collectors.toList());
        List<BookmarkDTO> bookmarkDTO = new ArrayList<>();
        for(int i=0; i<pets.size(); i++){
            bookmarkDTO.add(new BookmarkDTO(petDtoList.get(i), times.get(i)));
        }
        displayBookmarkPresenter.displayPetsOutput(new DisplayBookmarkPetsOutputData(bookmarkDTO));
    }
}
