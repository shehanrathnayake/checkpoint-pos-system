package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.common.UserDetails;
import lk.ijse.dep11.app.tm.User;
import lk.ijse.dep11.app.tm.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDataAccess {
    private static final PreparedStatement STM_GET_USER;
    private static final PreparedStatement STM_FIND_USERS;
    private static final PreparedStatement STM_GET_USER_ROLE;
    private static final PreparedStatement STM_FIND_USER_ROLES;
    private static final PreparedStatement STM_SET_USER;

    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_GET_USER = connection.prepareStatement("SELECT * FROM user WHERE user_id=?");
            STM_FIND_USERS = connection.prepareStatement("SELECT * FROM user WHERE user_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR gender LIKE ?");
            STM_GET_USER_ROLE = connection.prepareStatement("SELECT role FROM user_role WHERE id = ?");
            STM_FIND_USER_ROLES = connection.prepareStatement("SELECT * FROM user_role WHERE role LIKE ?");
            STM_SET_USER = connection.prepareStatement("INSERT INTO user (user_id, first_name, last_name, password, user_role_id, gender) VALUES (?,?,?,?,?,?)");
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
        String gender = rst.getString("gender");

        return new User(userId, firstName, lastName, password, userRoleId, gender);
    }

    public static List<User> findUsers(String query) throws SQLException {
        for (int i = 1; i < 5; i++) {
            STM_FIND_USERS.setString(i, "%".concat(query).concat("%"));
        }
        ResultSet rst = STM_FIND_USERS.executeQuery();
        List<User> userList = null;
        while (rst.next()) {
            userList.add(getUser(rst.getString("user_id")));
        }
        return userList;
    }

    public static UserRole getUserRole(int userRoleId) throws SQLException {
        STM_GET_USER_ROLE.setInt(1, userRoleId);
        ResultSet rst = STM_GET_USER_ROLE.executeQuery();
        UserRole userRole = null;
        if (rst.next()) {
            userRole = new UserRole(rst.getInt("id"), rst.getString("role"));
        }

        return userRole;
    }

    public static List<UserRole> findUserRoles(String query) throws SQLException {
        STM_FIND_USER_ROLES.setString(1, "%".concat(query).concat("%"));
        ResultSet rst = STM_FIND_USER_ROLES.executeQuery();
        List<UserRole> userRoleList = null;
        while(rst.next()) {
            userRoleList.add(new UserRole(rst.getInt("id"), rst.getString("role")));
        }
        return userRoleList;
    }

    public static void setUser(User newUser) throws SQLException {
        STM_SET_USER.setString(1, newUser.getId());
        STM_SET_USER.setString(2, newUser.getFirstName());
        STM_SET_USER.setString(3, newUser.getLastName());
        STM_SET_USER.setString(4, newUser.getPassword());
        STM_SET_USER.setInt(5, newUser.getUserRoleId());
        STM_SET_USER.setString(6, newUser.getGender());
        STM_SET_USER.executeUpdate();
    }
}
