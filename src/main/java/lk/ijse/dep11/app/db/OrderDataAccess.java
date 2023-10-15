package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Item;
import lk.ijse.dep11.app.tm.OrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class OrderDataAccess {
    private static final PreparedStatement GET_LAST_ORDER_ID;
    private static final PreparedStatement GET_ALL_ITEMS;
    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            GET_LAST_ORDER_ID = connection.prepareStatement("SELECT order_id FROM `order` ORDER BY order_id DESC LIMIT 1");
            GET_ALL_ITEMS = connection.prepareStatement("SELECT * FROM item");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<OrderItem> getAllOrderItems() {
        return null;
    }

    public static String getLastOrderId() throws SQLException {
        ResultSet rst = GET_LAST_ORDER_ID.executeQuery();
        if (!rst.next()) return "OD00000";
        String orderId = rst.getString("order_id");
        return orderId;
    }

    public static List<Item> getAllItems() throws SQLException {
        ResultSet rst = GET_ALL_ITEMS.executeQuery();
        ArrayList<Item> itemList = new ArrayList<>();
        while (rst.next()) {
            String itemCode = rst.getString("item_code");
            String description = rst.getString("description");
            int qty = rst.getInt("qty");
            BigDecimal unitPrice = rst.getBigDecimal("unit_price");
            itemList.add(new Item(itemCode, description, qty, unitPrice));
        }
        return itemList;
    }
}
