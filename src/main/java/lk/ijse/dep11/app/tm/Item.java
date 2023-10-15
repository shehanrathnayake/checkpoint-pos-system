package lk.ijse.dep11.app.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor
public class Item implements Serializable {
    private String itemCode;
    private String description;
    private int qtyOnHand;
    private BigDecimal unitPrice;

    @Override
    public String toString() {
        return itemCode;
    }
}
