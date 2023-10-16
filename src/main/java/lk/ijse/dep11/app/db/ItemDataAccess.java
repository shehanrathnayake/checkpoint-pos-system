package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDataAccess {
    private static final PreparedStatement STM_FIND_ITEMS;
    private static final PreparedStatement STM_SET_ITEM;
    private static final PreparedStatement STM_ITEM_EXISTS;
    private static final PreparedStatement STM_UPDATE_ITEM;
    private static final PreparedStatement STM_DELETE_ITEM;

    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_FIND_ITEMS = connection.prepareStatement("SELECT * FROM item WHERE item_code LIKE ? OR description LIKE ?");
            STM_SET_ITEM = connection.prepareStatement("INSERT INTO item (item_code, description, qty, unit_price) VALUES (?,?,?,?)");
            STM_ITEM_EXISTS = connection.prepareStatement("SELECT item_code FROM order_item WHERE item_code=?");
            STM_UPDATE_ITEM = connection.prepareStatement("UPDATE item SET description=?, qty=?, unit_price=? WHERE item_code=?");
            STM_DELETE_ITEM = connection.prepareStatement("DELETE FROM item WHERE item_code=?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Item> findItems(String query) throws SQLException {
        for (int i = 1; i < 3; i++) {
            STM_FIND_ITEMS.setString(i, "%".concat(query).concat("%"));
        }
        ResultSet rst = STM_FIND_ITEMS.executeQuery();
        List<Item> itemList = new ArrayList<>();
        while (rst.next()) {
            if (rst==null) return null;
            String itemCode = rst.getString("item_code");
            String description = rst.getString("description");
            int qty = rst.getInt("qty");
            BigDecimal unitPrice = rst.getBigDecimal("unit_price");
            itemList.add(new Item(itemCode, description, qty, unitPrice));
        }
        return itemList;
    }

    public static void setItem(Item newItem) throws SQLException {
        STM_SET_ITEM.setString(1, newItem.getItemCode());
        STM_SET_ITEM.setString(2, newItem.getDescription());
        STM_SET_ITEM.setInt(3, newItem.getQtyOnHand());
        STM_SET_ITEM.setBigDecimal(4, newItem.getUnitPrice());
        STM_SET_ITEM.executeUpdate();
    }

    public static boolean itemExist(String itemCode) throws SQLException {
        STM_ITEM_EXISTS.setString(1, itemCode);
        ResultSet rst = STM_ITEM_EXISTS.executeQuery();
        if (rst.next()) return true;
        else return false;
    }

    public static void updateItem(Item updatedItem) throws SQLException {
        STM_UPDATE_ITEM.setString(1, updatedItem.getDescription());
        STM_UPDATE_ITEM.setInt(2, updatedItem.getQtyOnHand());
        STM_UPDATE_ITEM.setBigDecimal(3, updatedItem.getUnitPrice());
        STM_UPDATE_ITEM.setString(4, updatedItem.getItemCode());
        STM_UPDATE_ITEM.executeUpdate();
    }

    public static void deleteItem(String itemCode) throws SQLException {
        STM_DELETE_ITEM.setString(1, itemCode);
        STM_DELETE_ITEM.executeUpdate();
    }
}
