package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataSQL extends UserDataAccess {

    public UserDataSQL() {
        try{
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public UserData findUser(String username) throws DataAccessException {
        System.out.println("Searching for user: " + username);

        try (var conn = DatabaseManager.getConnection()) {

            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {

                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {

                    if (rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        System.out.println("FOUND USER: " + username);
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error finding user: " + e);
        }
        return null;
    }

    public void createUser(UserData user) throws DataAccessException {
        System.out.println("CREATE USER " + user + "\n");

        String data = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(data)) {

            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());
            statement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }

    }

    public void clear(){
        System.out.println("USER CLEAR\n");

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE users ")) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
           return;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              PRIMARY KEY (`username`)
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
