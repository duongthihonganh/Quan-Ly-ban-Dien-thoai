package com.phonestore.dao;

import com.phonestore.model.Review;
import com.phonestore.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public List<Review> getReviewsByProduct(int productId) {
        List<Review> list = new ArrayList<>();
        String query = "SELECT r.*, u.username as userName FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review r = extractReview(rs);
                r.setUserName(rs.getString("userName"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách đánh giá: " + e.getMessage());
        }
        return list;
    }

    public List<Review> getAllReviews() {
        List<Review> list = new ArrayList<>();
        String query = "SELECT r.*, u.username as userName, p.name as productName FROM reviews r JOIN users u ON r.user_id = u.id JOIN products p ON r.product_id = p.id ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Review r = extractReview(rs);
                r.setUserName(rs.getString("userName"));
                r.setProductName(rs.getString("productName"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tất cả đánh giá: " + e.getMessage());
        }
        return list;
    }

    private Review extractReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        try { r.setAdminReply(rs.getString("admin_reply")); } catch (Exception ignored) {}
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }

    public boolean addReview(Review r) {
        String query = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getProductId());
            ps.setInt(3, r.getRating());
            ps.setString(4, r.getComment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm đánh giá: " + e.getMessage());
        }
        return false;
    }

    public boolean replyReview(int reviewId, String reply) {
        String query = "UPDATE reviews SET admin_reply = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, reply);
            ps.setInt(2, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi phản hồi đánh giá: " + e.getMessage());
        }
        return false;
    }
}
