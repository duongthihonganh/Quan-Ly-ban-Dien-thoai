package com.phonestore.dao;

import com.phonestore.model.Brand;
import com.phonestore.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String query = "SELECT * FROM brands";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                brands.add(new Brand(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách hãng: " + e.getMessage());
        }
        return brands;
    }

    public boolean addBrand(Brand brand) {
        String query = "INSERT INTO brands (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, brand.getName());
            ps.setString(2, brand.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm hãng: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBrand(Brand brand) {
        String query = "UPDATE brands SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, brand.getName());
            ps.setString(2, brand.getDescription());
            ps.setInt(3, brand.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật hãng: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteBrand(int id) {
        String query = "DELETE FROM brands WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa hãng (có thể do ràng buộc sản phẩm): " + e.getMessage());
        }
        return false;
    }
}
