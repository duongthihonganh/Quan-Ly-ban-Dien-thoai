package com.phonestore.dao;

import com.phonestore.model.Product;
import com.phonestore.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        return getProductsByQuery("SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE");
    }

    public List<Product> getAllProductsAdmin() {
        return getProductsByQuery("SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id");
    }

    public List<Product> searchProducts(String keyword) {
        String query = "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE AND p.name LIKE ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm sản phẩm: " + e.getMessage());
        }
        return list;
    }

    public Product getProductById(int id) {
        String query = "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy sản phẩm: " + e.getMessage());
        }
        return null;
    }

    public boolean addProduct(Product product) {
        String query = "INSERT INTO products (brand_id, name, price, stock, specs, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, product.getBrandId());
            ps.setString(2, product.getName());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getSpecs());
            ps.setBoolean(6, product.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sản phẩm: " + e.getMessage());
        }
        return false;
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET brand_id = ?, name = ?, price = ?, stock = ?, specs = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, product.getBrandId());
            ps.setString(2, product.getName());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getSpecs());
            ps.setBoolean(6, product.isActive());
            ps.setInt(7, product.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật sản phẩm: " + e.getMessage());
        }
        return false;
    }

    private List<Product> getProductsByQuery(String query) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi Query sản phẩm: " + e.getMessage());
        }
        return products;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product p = new Product(
                rs.getInt("id"),
                rs.getInt("brand_id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                rs.getString("specs"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at")
        );
        p.setBrandName(rs.getString("brandName"));
        return p;
    }
}
