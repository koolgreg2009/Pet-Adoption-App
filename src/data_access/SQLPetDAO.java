package data_access;

import entity.Pet;
import entity.preference.UserPreference;

import java.sql.*;
import java.util.ArrayList;


public class SQLPetDAO extends RescueAPIPetGet implements PetDAOInterface{
    /**
     * SQL Implementation of PetDAO
     */
    private final Connection conn;

    public SQLPetDAO(Connection conn){
        this.conn = conn;

    }

    /**
     * Insert Pet object into SQL table if pet_id doesn't already exist, else updates.
     * @param pet the {@link Pet} object to be saved.
     */
    @Override
    public void save(Pet pet) {

        try (PreparedStatement stmt = conn.prepareStatement(
                """
                    INSERT INTO pets (pet_id, name, owner, email, phone_num, species, pet_age, breed, bio, activity_level, gender, location, is_available, img_url)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO UPDATE SET
                        name = excluded.name,
                        owner = excluded.owner,
                        email = excluded.email,
                        phone_num = excluded.phone_num,
                        species = excluded.species,
                        pet_age = excluded.pet_age,
                        breed = excluded.breed,
                        bio = excluded.bio,
                        activity_level = excluded.activity_level,
                        gender = excluded.gender,
                        location = excluded.location,
                        is_available = excluded.is_available,
                        img_url = excluded.img_url""")) {

            stmt.setInt(1, pet.getPetID());                      // pet_id
            stmt.setString(2, pet.getName());                    // name
            stmt.setString(3, pet.getOwner());                   // owner
            stmt.setString(4, pet.getEmail());                   // email
            stmt.setString(5, pet.getPhoneNum());                // phone_num
            stmt.setString(6, pet.getSpecies());                 // species
            stmt.setInt(7, pet.getPetAge());                     // pet_age
            stmt.setString(8, pet.getBreed());                   // breed
            stmt.setString(9, pet.getBio());                     // bio
            stmt.setString(10, pet.getActivityLevel());          // activity_level
            stmt.setString(11, pet.getGender());                 // gender
            stmt.setString(12, pet.getLocation());               // location
            stmt.setInt(13, pet.isAvailable() ? 1 : 0);          // is_available (convert boolean to int)
            stmt.setString(14, pet.getImgUrl());                 // img_url

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert pet", e);
        }

    }

    @Override
    /**
     * Get Pet object based on pet id.
     */
    public Pet get(int petID) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM pets WHERE pet_id = ?")) {

            stmt.setInt(1, petID); // set petID into the SQL statement

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Pet(
                            rs.getString("owner"),
                            rs.getString("email"),
                            rs.getString("phone_num"),
                            rs.getInt("pet_id"),
                            rs.getString("species"),
                            rs.getInt("pet_age"),
                            rs.getString("breed"),
                            rs.getString("gender"),
                            rs.getString("activity_level"),
                            rs.getString("bio"),
                            rs.getString("location"),
                            rs.getInt("is_available") == 1,
                            rs.getString("name"),
                            rs.getString("img_url")
                    );
                } else {
                    return null; // not found
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get pet", e);
        }
    }

    /**
     * Filter pets directly for callers that already have a preference object.
     */
    @Override
    public ArrayList<Pet> getPreferencePets(UserPreference userPreference) {
        ArrayList<Pet> pets = new ArrayList<>();
        try (Statement statement = conn.createStatement();
             ResultSet rows = statement.executeQuery("SELECT pet_id FROM pets WHERE is_available = 1")) {
            while (rows.next()) {
                Pet pet = get(rows.getInt(1));
                if (FilePetDAO.matchesPreference(pet, userPreference)) pets.add(pet);
            }
            return pets;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter pets", e);
        }
    }

    /**
     * Helper method to check if pet table is empty
     * @return bool
     */
    private boolean isPetTableEmpty() {
        String sql = "SELECT COUNT(*) FROM pets";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1); // first column
                return count == 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check pet table count", e);
        }

        return true;
    }

}
