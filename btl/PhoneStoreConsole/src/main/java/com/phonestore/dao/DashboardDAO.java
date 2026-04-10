package com.phonestore.dao;

import com.phonestore.model.Dashboard;
import com.phonestore.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public DashboardDAO() {
    }

    public Dashboard getDashboardStats() {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int completedOrders = 0;
        int currentPending = 0;

        String statsQuery = "SELECT status, SUM(final_amount) as total, COUNT(id) as count FROM orders WHERE status IN ('DELIVERED', 'PENDING') GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(statsQuery);
             ResultSet rs = ps.executeQuery()) {
            
            while(rs.next()) {
                String status = rs.getString("status");
                if ("DELIVERED".equals(status)) {
                    BigDecimal total = rs.getBigDecimal("total");
                    totalRevenue = total != null ? total : BigDecimal.ZERO;
                    completedOrders = rs.getInt("count");
                } else if ("PENDING".equals(status)) {
                    currentPending = rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê đơn hàng: " + e.getMessage());
        }

        List<String> topSelling = getTopSellingProducts(5);
        return new Dashboard(totalRevenue, completedOrders, currentPending, topSelling);
    }

    private List<String> getTopSellingProducts(int limit) {
        List<String> list = new ArrayList<>();
        String query = "SELECT p.name, SUM(oi.quantity) as total_sold " +
                       "FROM order_items oi " +
                       "JOIN orders o ON oi.order_id = o.id " +
                       "JOIN products p ON oi.product_id = p.id " +
                       "WHERE o.status = 'DELIVERED' " +
                       "GROUP BY p.id, p.name " +
                       "ORDER BY total_sold DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("name") + " - Đã bán: " + rs.getInt("total_sold") + " chiếc");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê điện thoại bán chạy: " + e.getMessage());
        }
        return list;
    }
}
