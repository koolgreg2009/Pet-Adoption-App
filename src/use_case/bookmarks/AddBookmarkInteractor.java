package use_case.bookmarks;
import data_access.UserDAOInterface;
import entity.Bookmark;
import entity.user.AdopterUser;
import java.time.LocalDateTime;

/**
 * Interactor responsible for adding a bookmark. This handles the business logic for adding bookmarks to the account
 * of the user.
 */
public class AddBookmarkInteractor implements BookmarkInputBoundary{

    final AddBookmarkOutputBoundary bookmarkPresenter;
    final UserDAOInterface fileUserDAO;

    /**
     * Constructor for AddBookmarkInteractor, which includes the bookmark output boundary, bookmark factory and
     * the file data access object for users.
     *
     * @param outputBoundary the output boundary to send results to presenter
     * @param fileUserDAO the data access object for the user data
     */

    public AddBookmarkInteractor(AddBookmarkOutputBoundary outputBoundary, UserDAOInterface fileUserDAO) {
        this.bookmarkPresenter = outputBoundary;
        this.fileUserDAO = fileUserDAO;
    }

    /**
     * Executes the addition of a bookmark for a user. This also gives an error message if bookmark is already created.
     * @param inputData the input data containing the username and pet ID.
     */
    public void execute(BookmarkInputData inputData) {
        if (fileUserDAO.userHasBookmark(inputData.getUsername(), inputData.getPetID())) {
            this.bookmarkPresenter.prepareErrorView("Bookmark already exists");
        } else{
            AdopterUser user = ((AdopterUser) fileUserDAO.get(inputData.getUsername()));
            LocalDateTime now = LocalDateTime.now();
            Bookmark bookmark = new Bookmark(inputData.getUsername(), inputData.getPetID(), now);
            user.addBookmark(bookmark); // this should call userdao
            fileUserDAO.addBookmark(inputData.getUsername(), inputData.getPetID(), now);
            BookmarkOutputData bookmarkOutputData = new BookmarkOutputData(user.getBookmarks(), bookmark,
                    null, user.getUsername());
            this.bookmarkPresenter.prepareSuccessView(bookmarkOutputData);
        }

    }


}

