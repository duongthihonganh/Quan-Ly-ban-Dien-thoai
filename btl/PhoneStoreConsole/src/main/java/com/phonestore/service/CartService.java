package com.phonestore.service;

import com.phonestore.dao.CartDAO;
import com.phonestore.dao.ProductDAO;
import com.phonestore.model.OrderItem;
import com.phonestore.model.Product;

import java.util.List;

public class CartService {
    private CartDAO cartDAO;
    private ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    public void addToCart(int userId, int productId, int quantity) {
        Product p = productDAO.getProductById(productId);
        if (p == null) {
            System.out.println("-> Lỗi: Không tìm thấy sản phẩm.");
            return;
        }

        // Kiểm tra tồn kho trước khi thêm
        List<OrderItem> currentCart = cartDAO.getCartByUser(userId);
        int currentQty = 0;
        for (OrderItem item : currentCart) {
            if (item.getProductId() == productId) {
                currentQty = item.getQuantity();
                break;
            }
        }
        
        if (p.getStock() < (currentQty + quantity)) {
            System.out.println("-> Lỗi: Bạn đã có " + currentQty + " sản phẩm trong giỏ. Tối đa chỉ có thể lấy thêm " + (p.getStock() - currentQty) + " sản phẩm.");
            return;
        }

        if (cartDAO.addToCart(userId, productId, quantity)) {
            System.out.println("-> Đã thêm " + p.getName() + " vào giỏ hàng vĩnh viễn.");
        } else {
            System.out.println("-> Có lỗi xảy ra khi thêm vào giỏ.");
        }
    }

    public void removeFromCart(int userId, int productId) {
        if (cartDAO.removeFromCart(userId, productId)) {
            System.out.println("-> Đã xóa khỏi giỏ hàng.");
        } else {
            System.out.println("-> Chưa xóa được sản phẩm.");
        }
    }

    public List<OrderItem> getCartList(int userId) {
        return cartDAO.getCartByUser(userId);
    }

    public void clearCart(int userId) {
        cartDAO.clearCart(userId);
    }
}
