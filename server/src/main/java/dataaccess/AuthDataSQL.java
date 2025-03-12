package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthDataSQL extends AuthDataAccess {

    public AuthDataSQL(){

    }

    public void addAuthData(AuthData authData){

    }

    public boolean removeAuthData(String authToken){
        return false;
    }

    //returns the username
    public String validAuthToken(String authToken){
        return null;
    }

    public void clear(){

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
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
