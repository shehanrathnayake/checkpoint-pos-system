package lk.ijse.dep11.app.tm;

import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserData {
    private String UserId;
    private String name;
    private String gender;
    private String userRole;
    private transient Button btnEdit;

}
