package lk.ijse.dep11.app.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String password;
    private int userRoleId;
}
