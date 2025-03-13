package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthDataSQL extends AuthDataAccess {

    public AuthDataSQL(){
        try{
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAuthData(AuthData authData) {
        System.out.println("ADD AUTH\n");

        String data = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(data)) {

            statement.setString(1, authData.authToken());
            statement.setString(2, authData.username());
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            return;
        }

    }

    public boolean removeAuthData(String authToken){
        return false;
    }

    //returns the username
    public String validAuthToken(String authToken) throws DataAccessException {
        System.out.println("FIND AUTH\n");

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error finding user: " + e);
        }
        return null;
    }

    public void clear(){
        System.out.println("AUTH CLEAR\n");

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE auth ")) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            return;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )"""
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex){
            throw new DataAccessException("Database did not connect");
        }
    }


}
