package lk.ijse.dep11.app.db;

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
            STM_FIND_ORDERS = connection.prepareStatement("SELECT * FROM `order` WHERE order_id LIKE ? OR DATE_FORMAT(date, 'yyyy-MM-dd') LIKE ? OR user_id LIKE ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<OrderItem> getAllOrderItems() {
        return null;
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

    public static List<Order> findOrders(String query) throws SQLException {
        for (int i = 1; i < 4; i++) {
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
