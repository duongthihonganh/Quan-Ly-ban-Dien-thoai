package com.phonestore.dao;

import com.phonestore.model.Product;
import com.phonestore.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        return getProductsByQuery(
                "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE");
    }

    public List<Product> getAllProductsAdmin() {
        return getProductsByQuery(
                "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id");
    }

    public List<Product> searchProductsByName(String keyword) {
        String query = "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE AND p.name LIKE ?";
        return searchWithKeyword(query, keyword);
    }

    public List<Product> searchProductsBySpecs(String keyword) {
        String query = "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE AND p.specs LIKE ?";
        return searchWithKeyword(query, keyword);
    }

    public List<Product> getProductsByBrandName(String brandName) {
        String query = "SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE AND b.name LIKE ?";
        return searchWithKeyword(query, brandName);
    }

    private List<Product> searchWithKeyword(String query, String keyword) {
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

    public List<Product> getProductsByPriceRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        return getProductsByQuery("SELECT p.*, b.name as brandName FROM products p LEFT JOIN brands b ON p.brand_id = b.id WHERE p.is_active = TRUE AND p.price BETWEEN " + min + " AND " + max);
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
                rs.getTimestamp("created_at"));
        p.setBrandName(rs.getString("brandName"));
        return p;
    }

    public List<String> getTopSellingProducts(int limit) {
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
