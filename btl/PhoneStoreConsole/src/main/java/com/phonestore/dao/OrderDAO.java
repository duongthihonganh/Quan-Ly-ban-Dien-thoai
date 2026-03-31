package com.phonestore.dao;

import com.phonestore.model.Order;
import com.phonestore.model.OrderItem;
import com.phonestore.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrder(Order order, List<OrderItem> items) {
        String insertOrder = "INSERT INTO orders (user_id, total_amount, discount_amount, final_amount, status, shipping_address, payment_method, promotion_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        int orderId = -1;
        
        Connection conn = DBConnection.getConnection();
        if (conn == null) return orderId;

        try {
            conn.setAutoCommit(false); // Transaction

            // 1. Insert Order
            try (PreparedStatement ps = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                ps.setBigDecimal(2, order.getTotalAmount());
                ps.setBigDecimal(3, order.getDiscountAmount());
                ps.setBigDecimal(4, order.getFinalAmount());
                ps.setString(5, order.getStatus());
                ps.setString(6, order.getShippingAddress());
                ps.setString(7, order.getPaymentMethod());
                if (order.getPromotionId() != null) {
                    ps.setInt(8, order.getPromotionId());
                } else {
                    ps.setNull(8, Types.INTEGER);
                }
                
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }

            // 2. Insert Order Items and Update Stock
            if (orderId != -1) {
                String updateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";
                try (PreparedStatement psItem = conn.prepareStatement(insertItem);
                     PreparedStatement psStock = conn.prepareStatement(updateStock)) {
                    for (OrderItem item : items) {
                        psItem.setInt(1, orderId);
                        psItem.setInt(2, item.getProductId());
                        psItem.setInt(3, item.getQuantity());
                        psItem.setBigDecimal(4, item.getPrice());
                        psItem.addBatch();
                        
                        // Update stock
                        psStock.setInt(1, item.getQuantity());
                        psStock.setInt(2, item.getProductId());
                        psStock.addBatch();
                    }
                    psItem.executeBatch();
                    psStock.executeBatch();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Lỗi tạo đơn hàng (Transaction failed): " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Lỗi rollback: " + ex.getMessage());
            }
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
        return orderId;
    }

    public List<Order> getAllOrders() {
        return getOrdersQuery("SELECT * FROM orders ORDER BY created_at DESC");
    }

    public List<Order> getOrdersByUser(int userId) {
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        List<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy đơn hàng của user: " + e.getMessage());
        }
        return list;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái đơn: " + e.getMessage());
        }
        return false;
    }

    private List<Order> getOrdersQuery(String query) {
        List<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi Query Order: " + e.getMessage());
        }
        return list;
    }

    private Order extractOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        o.setFinalAmount(rs.getBigDecimal("final_amount"));
        o.setStatus(rs.getString("status"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPromotionId(rs.getInt("promotion_id"));
        if (rs.wasNull()) o.setPromotionId(null);
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
