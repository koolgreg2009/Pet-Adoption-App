# Pet Adoption App

### Contributors
* [Alex Lambermon](https://github.com/lamberm2)
* [Jane Li](https://github.com/snowykitkat)
* [Jenny Yang](https://github.com/yangje02)
* [Joy Cai](https://github.com/Joyce12345678)
* [Justin Yoon](https://github.com/justinyoon95)
* [Kevin Hu](https://github.com/koolgreg2009) 

<hr>

# About
Our project is a platform that lists verified and healthy pets for adoption.
The program will simulate as a pet adoption site in which users are able to scroll through pets that are up for adoption.
Users looking to adopt are able to view all pets currently available for adoption.
Each user will be prompted to make a profile with their unique identification on the webpage,
which they have to sign in to view the listed pets.
Additionally, users can set preferences on their profile page and use those preferences as a filter in the search.
The preference profile will include questions regarding the preferred pet type (cat for now), size, age, and
location to help find the perfect fit for the pet and the new owner.
Users can view pet listings that match their preference in the home page. They can interact with pet listings through 
ways such as adding/removing pets as bookmarks, viewing additional information about the pet, and adopting the pet. 
In the additional information page, users can click on the TextLabel "breed", in which information about the breed
will be displayed. 

In the home page, users can navigate to the notifications page, where they can view notification messages about if 
their bookmarked pet has been adopted, the preference page where they can change their preferences, as well as the 
bookmark page, where they can view bookmarked pets as pet listings. 

* This project was entirely written in Java.
* Java Swing was used for the GUI.
* This project strictly adheres to the Clean Architecture and SOLID design principles.

## Getting Started

Requires **JDK 17** and **Maven 3.9+**. The SQL code uses Java text blocks; the build consistently targets release 17.

From the repository root:

```sh
mvn clean verify
mvn exec:java
```

The app uses **SQLite** (`data.db` in the current working directory). Existing JSON files are preserved and are not automatically imported into SQLite. A fresh database has no default account or pets: sign up, then save blank preferences to browse everything. Do not empty or delete existing JSON files or databases to initialize the app; tables are created automatically.

Use an isolated directory for manual testing, and reuse the same directory when checking restart persistence:

```sh
mvn -Dpetadoption.dataDir=/tmp/pet-adoption-manual exec:java
```

Startup uses cached data and does not require an API or network connection. To import remote listings, configure `RESCUEGROUPS_API_KEY` in your environment, then run:

```sh
mvn -Dpetadoption.dataDir=/tmp/pet-adoption-manual exec:java -Dexec.args="--refresh-pets"
```

The explicit import runs before the window opens and can take time because each pet's shelter details are fetched separately. Requests have timeouts; an import failure reports an error in the launch console and leaves cached data available. Repeated imports use RescueGroups IDs and skip existing pets, preserving local adoption status. Legacy listings stored with generated IDs are not migrated by this pass.

TheCatAPI breed lookup uses optional `THECATAPI_API_KEY` from the environment. No credentials are stored in source. Failed lookups show a connection/configuration hint; pet images have a fallback. Breed and image requests run in background workers.

On this Mac, Maven is bundled with IntelliJ but is not on PATH. Use this executable in place of `mvn` if needed:

```sh
"/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn" clean verify
```

JUnit 5 runs through the pinned Surefire plugin, fails if no tests are found, and writes reports to `target/surefire-reports`. Tests use fixtures and temporary databases/files, without requiring live API credentials. Older relative-file tests run inside `target`, away from application data.

## APIs Used
* [RescueGroups.org](https://rescuegroups.org/services/adoptable-pet-data-api/): API that provides us adoptable pet data.
* [TheCATAPI](https://documenter.getpostman.com/view/5578104/RWgqUxxh#intro): API that returns cat breed information.

## Data persistence
* The project offers two forms of data storage systems:
  * Files
  * SQLite
* Respective DAO runs SQL queries to read and write entity information
* `Main.java` currently wires SQLite. JSON DAOs remain available for alternate wiring; switching storage requires code changes and does not migrate existing data. `FilePetDAO` starts empty offline; call `fetchAndStorePets()` explicitly to import.

### Files
* Stores entities in json form using DAO's with File prefix in users.json, pets.json, and data.json

### SQL Usage
* Stores User, Pet, Bookmark, UserPreference Entities in SQL tables within data.db
* Uses SQLite. No database setup required — app will create `.db` files on first run.
* run sqlite3 data.db to view contents of tables
* Two main entities are User and Pet, with respective primary keys username and pet_id. User to UserPreference is 1 to 1,
but stored in separate table as it's not a primitive datatype. User to bookmark is one to many, with primary key being
  (user, pet_id). 
* Misc data such as pet locations and breed names are stored in the locations and breeds table. 

# Technical Problems and Notes
N/A (Feel free to fill it out or check currently ongoing issues)
* [Issues](https://github.com/koolgreg2009/group8csc207/issues)

# Documents
* [UML Diagram](https://lucid.app/lucidchart/5fb0ce87-45b4-4f2e-9527-57cff33bf035/edit?viewport_loc=1226%2C-948%2C4386%2C2030%2C0_0&invitationId=inv_282f4d82-22a6-4b58-b2ca-4a74da2ebeb9)
* [Flow Chart](https://lucid.app/lucidchart/d2f8b40e-59de-49e9-9eeb-e733e1e2a166/edit?invitationId=inv_4ff0dbca-a473-4dc7-8d1f-96b53d3bb54a&page=0_0#)
* [Professor Paul Gries's CA Example](https://github.com/paulgries/LoginCleanArchitecture) (Our program is inspired by Paul Gries's user/password manager program example. Thank you!)

## First-pass reliability checks and limits

Automated regressions cover JSON/SQLite reopen persistence, preferences, bookmarks, adoption notifications, first-login browsing, repeat imports, malformed import data, and age validation. Existing use-case tests cover signup/login and the other workflows.

Manual checks (use the isolated directory above):

1. Launch without credentials or network; sign up and save empty preferences. Confirm the empty state and resize the window.
2. Import pets with your own RescueGroups credential. Log in, filter by breed/location/age, clear filters, bookmark twice, remove a bookmark, and open pet details.
3. Open breed information and images with the network unavailable; confirm the window stays responsive and reports failure or a fallback.
4. Bookmark a pet in two accounts, adopt it from one account, and verify it disappears from available listings/bookmarks and generates one notification for each account that bookmarked it.
5. Close and relaunch using the same data directory. Verify login, preferences, bookmarks, adoption status, and notifications persist.

Remaining work: local database operations still run on the EDT; larger datasets or locked databases may pause the UI. Adoption spans multiple writes without a shared transaction, so interrupted writes may leave partial state. Preference forms still need hydration from saved account settings on navigation, and broader layout/accessibility testing remains. Passwords are stored in plaintext as in the original course project. Previously committed API credentials should be rotated by their owners. Live API success paths require manual verification with valid credentials.
