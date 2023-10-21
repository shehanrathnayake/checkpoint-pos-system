package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Customer;
import lk.ijse.dep11.app.tm.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataAccess {
    private static final PreparedStatement STM_FIND_CUSTOMERS;
    private static final PreparedStatement STM_SET_CUSTOMER;
    private static final PreparedStatement STM_CUSTOMER_EXISTS;
    private static final PreparedStatement STM_UPDATE_CUSTOMER;
    private static final PreparedStatement STM_DELETE_CUSTOMER;
    private static final PreparedStatement STM_LAST_CUSTOMER_ID;
    private static final PreparedStatement STM_GET_CUSTOMER_ID;

    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_FIND_CUSTOMERS = connection.prepareStatement("SELECT * FROM customer WHERE customer_id LIKE ? OR name LIKE ? OR address LIKE ? OR phone LIKE ?");
            STM_SET_CUSTOMER = connection.prepareStatement("INSERT INTO customer (customer_id, name, address, phone) VALUES (?,?,?,?)");
            STM_CUSTOMER_EXISTS = connection.prepareStatement("SELECT customer_id FROM customer WHERE customer_id=?");
            STM_UPDATE_CUSTOMER = connection.prepareStatement("UPDATE customer SET name=?, address=?, phone=? WHERE customer_id=?");
            STM_DELETE_CUSTOMER = connection.prepareStatement("DELETE FROM customer WHERE customer_id=?");
            STM_LAST_CUSTOMER_ID = connection.prepareStatement("SELECT customer_id FROM customer ORDER BY customer_id DESC LIMIT 1");
            STM_GET_CUSTOMER_ID = connection.prepareStatement("SELECT customer_id FROM customer_order WHERE order_id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Customer> findCustomers(String query) throws SQLException {
        for (int i = 1; i < 5; i++) {
            STM_FIND_CUSTOMERS.setString(i, "%".concat(query).concat("%"));
        }
        ResultSet rst = STM_FIND_CUSTOMERS.executeQuery();
        List<Customer> customerList = new ArrayList<>();
        while (rst.next()) {
            String customerId = rst.getString("customer_id");
            String name = rst.getString("name");
            String address = rst.getString("address");
            String phone = rst.getString("phone");
            customerList.add(new Customer(customerId, name, address, phone));
        }
        return customerList;
    }

    public static void setCustomer(Customer newCustomer) throws SQLException {
        STM_SET_CUSTOMER.setString(1, newCustomer.getCustomerId());
        STM_SET_CUSTOMER.setString(2, newCustomer.getName());
        STM_SET_CUSTOMER.setString(3, newCustomer.getAddress());
        STM_SET_CUSTOMER.setString(4, newCustomer.getPhone());
        STM_SET_CUSTOMER.executeUpdate();
    }
    public static void updateCustomer(Customer updatedCustomer) throws SQLException {
        STM_UPDATE_CUSTOMER.setString(1, updatedCustomer.getName());
        STM_UPDATE_CUSTOMER.setString(2, updatedCustomer.getAddress());
        STM_UPDATE_CUSTOMER.setString(3, updatedCustomer.getPhone());
        STM_UPDATE_CUSTOMER.setString(4, updatedCustomer.getCustomerId());
        STM_UPDATE_CUSTOMER.executeUpdate();
    }

    public static String getLastCustomerId() throws SQLException {
        ResultSet rst = STM_LAST_CUSTOMER_ID.executeQuery();
        String lastCustomerId = "C00000";
        if (rst.next()) {
            lastCustomerId = rst.getString("customer_id");
        }

        return lastCustomerId;
    }

    public static void deleteCustomer(String customerId) throws SQLException {
        STM_DELETE_CUSTOMER.setString(1, customerId);
        STM_DELETE_CUSTOMER.executeUpdate();
    }

    public static String getCustomerId(String orderId) throws SQLException {
        STM_GET_CUSTOMER_ID.setString(1, orderId);
        ResultSet rst = STM_GET_CUSTOMER_ID.executeQuery();
        if (!rst.next()) return null;
        return rst.getString("customer_id");
    }


}
