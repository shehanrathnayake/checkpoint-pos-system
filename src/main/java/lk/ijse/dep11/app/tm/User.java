package lk.ijse.dep11.app.tm;

import lk.ijse.dep11.app.db.UserDataAccess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.SQLException;

@Data @NoArgsConstructor @AllArgsConstructor
public class User implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String password;
    private int userRoleId;
    private String gender;

    public String getName() {
        return firstName + " " + lastName;
    }

    public UserRole getUserRole() throws SQLException {
        return UserDataAccess.getUserRole(userRoleId);
    }
}
