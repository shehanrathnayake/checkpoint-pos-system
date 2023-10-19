package lk.ijse.dep11.app.tm;

import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.db.UserDataAccess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class Order implements Serializable {
    private String orderId;
    private String orderDate;
    private String userId;

    public String getUserName() throws SQLException {
        return UserDataAccess.getUser(userId).getName();
    }

    public String getCustomerId() throws SQLException {
        String customerId = CustomerDataAccess.getCustomerId(orderId);
        if (customerId==null) return "-";
        return customerId;
    }

    public String getCustomerName() throws SQLException {
        String customerId = getCustomerId();
        if (customerId.equals("-")) return "-";
        return CustomerDataAccess.findCustomers(customerId).get(0).getName();
    }

    public BigDecimal getTotal() throws SQLException {
        return OrderDataAccess.getTotal(orderId);
    }
}
