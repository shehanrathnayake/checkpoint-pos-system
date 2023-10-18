package lk.ijse.dep11.app.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserRole implements Serializable {
    private int id;
    private String role;

    @Override
    public String toString() {
        return role;
    }
}
