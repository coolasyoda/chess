package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class UserDataSQL extends UserDataAccess {

    public UserDataSQL() {
        try{
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public UserData findUser(String username){
        return null;
    }

    public void createUser(UserData user){

    }

    public void clear(){

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              PRIMARY KEY (`username`),
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
            throw new RuntimeException(ex);
        }
    }

}
