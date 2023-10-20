package lk.ijse.dep11.app.db;

import javafx.scene.control.Button;
import lk.ijse.dep11.app.tm.Item;
import lk.ijse.dep11.app.tm.Order;
import lk.ijse.dep11.app.tm.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class OrderDataAccess {
    private static final PreparedStatement STM_GET_LAST_ORDER_ID;
    private static final PreparedStatement STM_GET_ALL_ITEMS;
    private static final PreparedStatement STM_INSERT_ORDER_ITEM;
    private static final PreparedStatement STM_INSERT_ORDER;
    private static final PreparedStatement STM_UPDATE_STOCK;
    private static final PreparedStatement STM_GET_ORDER_ITEMS;
    private static final PreparedStatement STM_FIND_ORDERS;

    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_GET_LAST_ORDER_ID = connection.prepareStatement("SELECT order_id FROM `order` ORDER BY order_id DESC LIMIT 1");
            STM_GET_ALL_ITEMS = connection.prepareStatement("SELECT * FROM item");
            STM_INSERT_ORDER_ITEM = connection.prepareStatement("INSERT INTO order_item (item_code, order_id, qty, unit_price) VALUES (?,?,?,?)");
            STM_INSERT_ORDER = connection.prepareStatement("INSERT INTO `order` (order_id, date, user_id) VALUES (?,CURRENT_DATE,?)");
            STM_UPDATE_STOCK = connection.prepareStatement("UPDATE item SET qty=(qty+?) WHERE item_code=?");
            STM_GET_ORDER_ITEMS = connection.prepareStatement("SELECT * FROM order_item WHERE order_id = ?");
//            STM_FIND_ORDERS = connection.prepareStatement("SELECT * FROM `order` WHERE order_id LIKE ? OR DATE_FORMAT(date, 'yyyy-MM-dd') LIKE ? OR user_id LIKE ?");
            STM_FIND_ORDERS = connection.prepareStatement("SELECT o.order_id, o.date, SUM(oi.qty * oi.unit_price) AS total , o.user_id, CONCAT(u.first_name, ' ',  u.last_name) AS user_name,\n" +
                    "       co.customer_id, c.name AS customer_name FROM `order` AS o\n" +
                    "    INNER JOIN\n" +
                    "    order_item oi on o.order_id = oi.order_id\n" +
                    "    INNER JOIN\n" +
                    "    user AS u ON o.user_id = u.user_id\n" +
                    "    LEFT JOIN\n" +
                    "    customer_order AS co ON o.order_id = co.order_id\n" +
                    "    LEFT JOIN\n" +
                    "    customer AS c ON co.customer_id = c.customer_id\n" +
                    "    WHERE o.order_id LIKE ? OR CONCAT(o.date,'') LIKE ? OR o.user_id LIKE ? OR CONCAT(u.first_name, ' ',  u.last_name) LIKE ?\n" +
                    "    OR c.customer_id LIKE ? OR c.name LIKE ?\n" +
                    "    GROUP BY o.order_id, co.customer_id ORDER BY o.order_id DESC ;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getLastOrderId() throws SQLException {
        ResultSet rst = STM_GET_LAST_ORDER_ID.executeQuery();
        if (!rst.next()) return "OD00000";
        String orderId = rst.getString("order_id");
        return orderId;
    }

    public static List<Item> getAllItems() throws SQLException {
        ResultSet rst = STM_GET_ALL_ITEMS.executeQuery();
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

    public static void saveOrder(List<OrderItem>orderItemList, String orderId, String userId) throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
        try{
            STM_INSERT_ORDER.setString(1, orderId);
            STM_INSERT_ORDER.setString(2, userId);
            STM_INSERT_ORDER.executeUpdate();

            for (OrderItem orderItem : orderItemList) {
                STM_INSERT_ORDER_ITEM.setString(1, orderItem.getOrderItemCode());
                STM_INSERT_ORDER_ITEM.setString(2, orderId);
                STM_INSERT_ORDER_ITEM.setInt(3, orderItem.getQty());
                STM_INSERT_ORDER_ITEM.setBigDecimal(4, orderItem.getUnitPrice());
                STM_INSERT_ORDER_ITEM.executeUpdate();

                STM_UPDATE_STOCK.setInt(1, (-1 * orderItem.getQty()));
                STM_UPDATE_STOCK.setString(2, orderItem.getOrderItemCode());
                STM_UPDATE_STOCK.executeUpdate();
            }
        } catch (Throwable t) {
            SingleConnectionDataSource.getInstance().getConnection().rollback();
            throw new SQLException(t);
        } finally {
            SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
        }
    }

    public static BigDecimal getTotal(String orderId) throws SQLException {
        STM_GET_ORDER_ITEMS.setString(1, orderId);
        ResultSet rst = STM_GET_ORDER_ITEMS.executeQuery();
        BigDecimal orderTotal = BigDecimal.ZERO;
        while(rst.next()) {
            orderTotal = orderTotal.add(rst.getBigDecimal("unit_price").multiply(new BigDecimal(rst.getInt("qty"))));
        }
        return orderTotal;
    }
    public static List<OrderItem> getAllOrderItems(String orderId) throws SQLException {
        STM_GET_ORDER_ITEMS.setString(1, orderId);
        ResultSet rst = STM_GET_ORDER_ITEMS.executeQuery();
        List<OrderItem> orderItemList = new ArrayList<>();
        while(rst.next()) {
            String itemCode = rst.getString("item_code");
            String description = ItemDataAccess.getDescription(itemCode);
            int qty = rst.getInt("qty");
            BigDecimal unitPrice = rst.getBigDecimal("unit_price");
            orderItemList.add(new OrderItem(itemCode, description, qty, unitPrice, BigDecimal.ZERO, new Button("DELETE")));
        }
        return orderItemList;
    }

    public static List<Order> findOrders(String query) throws SQLException {
        for (int i = 1; i < 7; i++) {
            STM_FIND_ORDERS.setString(i, "%".concat(query).concat("%"));
        }
        ResultSet rst = STM_FIND_ORDERS.executeQuery();
        List<Order> orderList = new ArrayList<>();
        while(rst.next()) {
            String orderId = rst.getString("order_id");
            String date = rst.getDate("date").toString();
            String userId = rst.getString("user_id");
            orderList.add(new Order(orderId, date, userId));
        }
        return orderList;
    }
}
