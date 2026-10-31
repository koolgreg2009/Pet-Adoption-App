package use_case.bookmarks;

import data_access.PetDAOInterface;
import data_access.UserDAOInterface;
import dto.BookmarkDTO;
import dto.PetDTO;
import entity.Bookmark;
import entity.Pet;
import entity.user.AdopterUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is an interactor for removing a bookmark from a user's list of bookmarks.
 */

public class RemoveBookmarkInteractor implements BookmarkInputBoundary {

    final UserDAOInterface userDAO;
    final RemoveBookmarkOutputBoundary removeOutputBoundary;
    private final PetDAOInterface filePetDAO;

    /**
     * Constructs a RemoveBookmarkInteractor with the specified user DAO and output boundary.
     * @param userDAO the data access object for the user
     * @param removeOutputBoundary the output boundary to handle the output of the bookmark removal process
     */
    public RemoveBookmarkInteractor(UserDAOInterface userDAO, RemoveBookmarkOutputBoundary removeOutputBoundary, PetDAOInterface filepetDAO) {
        this.userDAO = userDAO;
        this.removeOutputBoundary = removeOutputBoundary;
        this.filePetDAO = filepetDAO;

    }

    /**
     * Executes the removal a bookmark based on the input data given.
     * @param inputData the data required to remove the bookmark.
     */
    public void execute(BookmarkInputData inputData){
        AdopterUser user = ((AdopterUser) userDAO.get(inputData.getUsername()));
        ArrayList<Bookmark> userBookmarks = (ArrayList<Bookmark>) user.getBookmarks();
        Bookmark bookmarkToRemove = null;
        for (Bookmark userBookmark : userBookmarks) {
            if (userBookmark.getPetID() == inputData.getPetID()){
                bookmarkToRemove = userBookmark;
                break;
            }
        }

        user.removeBookmark(bookmarkToRemove);
        userDAO.removeBookmark(user.getUsername(), inputData.getPetID());
        List<Pet> pets = new ArrayList<>();
        List<LocalDateTime> times = new ArrayList<>();
        for(Bookmark bookmark: user.getBookmarks()){
            Pet pet = filePetDAO.get(bookmark.getPetID());
            if (pet != null && pet.isAvailable()) {
                pets.add(pet);
                times.add(bookmark.getBookmarkedDate());
            }
        }
        List<PetDTO> petDtoList = pets.stream()
                .map(pet -> new PetDTO(pet.getPetID(), pet.getName(), pet.getBreed(), pet.getGender(),
                        pet.getSpecies(), pet.getPetAge(), pet.getBio(), pet.getOwner(), pet.getEmail(),
                        pet.getPhoneNum(), pet.getActivityLevel(), pet.getLocation(), pet.getImgUrl()))
                .collect(Collectors.toList());
        List<BookmarkDTO> bookmarkDTO = new ArrayList<>();
        for(int i=0; i<pets.size(); i++){
            bookmarkDTO.add(new BookmarkDTO(petDtoList.get(i), times.get(i)));
        }


        BookmarkOutputData bookmarkOutputData = new BookmarkOutputData(userBookmarks, bookmarkToRemove, bookmarkDTO, user.getUsername());
        this.removeOutputBoundary.prepareSuccessView(bookmarkOutputData);
    }
}
