package lk.ijse.dep11.app.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor
public class Customer implements Serializable {
    private String customerId;
    private String name;
    private String address;
    private String phone;
}
