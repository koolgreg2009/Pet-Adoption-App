package data_access;


import com.fasterxml.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class SQLAPIInfoDao extends RescueAPIBase implements APIInfoInterface{
    private final Connection conn;

    public SQLAPIInfoDao(Connection conn) throws SQLException {
        this.conn = conn;

    }

    /**
     * Retrieves a list of data associated with the specified key.
     *
     * @param table to indicate which table to retrieve from
     * @return a list of strings representing the data associated with the specified key.
     */
    @Override
    public List<String> getData(String table) {
        List<String> dataList = new ArrayList<>();
        String sql = "SELECT data FROM " + table;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dataList.add(rs.getString("data"));
            }
            return dataList;
        } catch (SQLException e){
            throw new RuntimeException("Couldn't retrieve data from " + table + e);
        }
    }
    /**
     * Retreives all breeds in rescuesAPI database
     * @throws IOException
     */
    @Override
    public void getBreedInfo() throws IOException{
        String response = makeAPICall("/public/animals/breeds/search/cats?limit=250");
        JsonNode root = getObjectMapper().readTree(response);
        JsonNode data = root.get("data");
        for (JsonNode node : data) {
            String breedName = node.get("attributes").get("name").asText();
            String sql = """
                    INSERT OR IGNORE INTO breeds (data) VALUES (?)
                    """;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, breedName);
                pstmt.executeUpdate();
            } catch (SQLException e){
                throw new RuntimeException("Couldn't insert breed " + breedName + e);
            }
        }
    }

    /**
     * Retrieves the 250 of cat locations in database
     * @throws IOException
     */
    @Override
    public void getLocation() throws IOException {
        String response = makeAPICall("/public/animals/search/available/cats?limit=250");
        JsonNode root = getObjectMapper().readTree(response);
        JsonNode includedArray = root.path("included");
        if (includedArray.isArray()) {
            for (JsonNode item : includedArray) {
                if ("locations".equals(item.path("type").asText())) {
                    String cityState = item.path("attributes").path("citystate").asText();
                    if (!Objects.equals(cityState, "")){
                        //insert("locations", cityState);
                        String sql = """
                        INSERT OR IGNORE INTO locations (data) VALUES (?)
                        """;
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)){
                                pstmt.setString(1, cityState);
                                pstmt.executeUpdate();
                            } catch (SQLException e){
                                throw new RuntimeException("Couldn't insert breed " + cityState + e);
                            }
                    }
                }
            }
        }
    }

    /**
     * Checks if a specific string exists in the data associated with the specified key.
     *
     * @param value the string to check for existence.
     * @param table which table to query
     * @return {@code true} if the string exists, {@code false} otherwise.
     */
    @Override
    public boolean exists(String value, String table) {
        String sql = "SELECT 1 FROM " + table + " WHERE data = ? LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if it exists
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence in " + table, e);
        }
    }

    @Override
    public boolean exists(List<String> values, String table) {
        for (String value : values) {
            if (!exists(value, table)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTableEmpty(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1); // first column
                return count == 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check table count for: " + tableName, e);
        }

        return true; // fallback, shouldn't hit this
    }
}
