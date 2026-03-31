package com.phonestore.dao;

import com.phonestore.model.Promotion;
import com.phonestore.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    public Promotion getPromotionByCode(String code) {
        String query = "SELECT * FROM promotions WHERE code = ? AND is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Promotion p = new Promotion();
                p.setId(rs.getInt("id"));
                p.setCode(rs.getString("code"));
                p.setDiscountPercent(rs.getInt("discount_percent"));
                p.setActive(rs.getBoolean("is_active"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mã khuyến mãi: " + e.getMessage());
        }
        return null;
    }

    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        String query = "SELECT * FROM promotions";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Promotion p = new Promotion();
                p.setId(rs.getInt("id"));
                p.setCode(rs.getString("code"));
                p.setDiscountPercent(rs.getInt("discount_percent"));
                p.setActive(rs.getBoolean("is_active"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi hiện khuyến mãi: " + e.getMessage());
        }
        return list;
    }

    public boolean addPromotion(Promotion p) {
        String query = "INSERT INTO promotions (code, discount_percent, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, p.getCode());
            ps.setInt(2, p.getDiscountPercent());
            ps.setBoolean(3, p.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm khuyến mãi: " + e.getMessage());
        }
        return false;
    }
}
