package lk.ijse.dep11.app.tm;

import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Serializable;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class OrderItem implements Serializable {
    private String orderItemCode;
    private String description;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private Button btnDelete;

    public BigDecimal getTotal() {
        return unitPrice.multiply(new BigDecimal(qty)).setScale(2);
    }
}
