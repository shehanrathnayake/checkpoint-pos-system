package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataAccess {
    private static final PreparedStatement STM_GET_USER;

    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_GET_USER = connection.prepareStatement("SELECT * FROM user WHERE user_id=?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static User getUser(String enteredUserId) throws SQLException {
        STM_GET_USER.setString(1, enteredUserId);
        ResultSet rst = STM_GET_USER.executeQuery();
        if (!rst.next()) return null;

        String userId = rst.getString("user_id");
        String firstName = rst.getString("first_name");
        String lastName = rst.getString("last_name");
        String password = rst.getString("password");
        int userRoleId = rst.getInt("user_role_id");

        return new User(userId, firstName, lastName, password, userRoleId);

    }
}
