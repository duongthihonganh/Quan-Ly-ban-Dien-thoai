package com.phonestore.view;

import com.phonestore.dao.*;
import com.phonestore.model.*;
import com.phonestore.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class CustomerView {
    private Scanner scanner;
    private User customer;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private PromotionDAO promotionDAO;
    private ReviewDAO reviewDAO;
    private CartService cartService;

    public CustomerView(Scanner scanner, User customer) {
        this.scanner = scanner;
        this.customer = customer;
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.promotionDAO = new PromotionDAO();
        this.reviewDAO = new ReviewDAO();
        this.cartService = new CartService();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== CỬA HÀNG ĐIỆN THOẠI ===");
            System.out.println("1. Xem tất cả sản phẩm");
            System.out.println("2. Tìm kiếm sản phẩm");
            System.out.println("3. Xem chi tiết & Đánh giá SP");
            System.out.println("4. Xem Giỏ hàng (" + cartService.getCartList(customer.getId()).size() + ")");
            System.out.println("5. Lịch sử mua hàng");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn chức năng: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": browseProducts(); break;
                case "2": searchProducts(); break;
                case "3": viewProductDetails(); break;
                case "4": manageCart(); break;
                case "5": orderHistory(); break;
                case "0":
                    System.out.println("Đã đăng xuất.");
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    private void browseProducts() {
        System.out.println("\n-- DANH SÁCH SẢN PHẨM --");
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            System.out.println(p);
        }
        System.out.print("Nhập ID Sản phẩm để MUA (hoặc 0 để quay lại): ");
        int pId = Integer.parseInt(scanner.nextLine());
        if (pId != 0) {
            System.out.print("Số lượng: ");
            int qty = Integer.parseInt(scanner.nextLine());
            cartService.addToCart(customer.getId(), pId, qty);
        }
    }

    private void searchProducts() {
        System.out.print("Nhập từ khóa cấu hình/tên: ");
        String kw = scanner.nextLine();
        List<Product> products = productDAO.searchProducts(kw);
        if (products.isEmpty()) {
            System.out.println("Không tìm thấy!");
        } else {
            for (Product p : products) {
                System.out.println(p);
            }
        }
    }

    private void viewProductDetails() {
        System.out.print("Nhập ID Sản phẩm để xem chi tiết: ");
        int pId = Integer.parseInt(scanner.nextLine());
        Product p = productDAO.getProductById(pId);
        if (p != null) {
            System.out.println("\n--- Chi tiết ---");
            System.out.println("Tên: " + p.getName());
            System.out.println("Giá: " + p.getPrice());
            System.out.println("Cấu hình: " + p.getSpecs());
            System.out.println("Tồn kho: " + p.getStock());
            
            System.out.println("\n[ Đánh giá của khách hàng ]");
            List<Review> reviews = reviewDAO.getReviewsByProduct(pId);
            if (reviews.isEmpty()) System.out.println("(Chưa có đánh giá nào)");
            for (Review r : reviews) {
                System.out.println(r.getRating() + " sao: " + r.getComment());
            }

            System.out.println("\n1. Đánh giá | 2. Thêm vào giỏ | 0. Thoát");
            String c = scanner.nextLine();
            if ("1".equals(c)) {
                System.out.print("Điểm (1-5): "); int score = Integer.parseInt(scanner.nextLine());
                System.out.print("Bình luận: "); String cmt = scanner.nextLine();
                Review nr = new Review(); nr.setUserId(customer.getId()); nr.setProductId(pId);
                nr.setRating(score); nr.setComment(cmt);
                reviewDAO.addReview(nr);
                System.out.println("Đã gửi đánh giá.");
            } else if ("2".equals(c)) {
                System.out.print("Số lượng: "); int qty = Integer.parseInt(scanner.nextLine());
                cartService.addToCart(customer.getId(), pId, qty);
            }
        }
    }

    private void manageCart() {
        System.out.println("\n-- GIỎ HÀNG CỦA BẠN --");
        List<OrderItem> items = cartService.getCartList(customer.getId());
        if (items.isEmpty()) {
            System.out.println("Giỏ hàng trống.");
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal sub = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            System.out.printf("[%d] %s | Số lượng: %d | Đơn giá: %s | Tổng: %s%n", item.getProductId(), item.getProductName(), item.getQuantity(), item.getPrice(), sub);
            total = total.add(sub);
        }
        System.out.println("Tạm tính: " + total + " VNĐ");
        System.out.println("1. Thanh toán | 2. Xóa SP khỏi giỏ | 0. Quay lại");
        System.out.print("Chọn: ");
        String c = scanner.nextLine();
        
        if ("1".equals(c)) {
            checkout(total, items);
        } else if ("2".equals(c)) {
            System.out.print("Nhập SpID cần xóa: ");
            int delId = Integer.parseInt(scanner.nextLine());
            cartService.removeFromCart(customer.getId(), delId);
        }
    }

    private void checkout(BigDecimal total, List<OrderItem> items) {
        System.out.println("\n-- THANH TOÁN --");
        System.out.print("Nhập địa chỉ giao hàng (Mặc định: " + customer.getAddress() + "): ");
        String address = scanner.nextLine();
        if (address.isEmpty()) address = customer.getAddress();

        System.out.print("Phương thức thanh toán (COD / BANK): ");
        String method = scanner.nextLine();
        if (method.isEmpty()) method = "COD";

        System.out.print("Mã khuyến mãi (bỏ trống nếu không có): ");
        String promo = scanner.nextLine();
        
        BigDecimal finalTotal = total;
        BigDecimal discount = BigDecimal.ZERO;
        Integer promoId = null;

        if (!promo.isEmpty()) {
            Promotion p = promotionDAO.getPromotionByCode(promo);
            if (p != null) {
                promoId = p.getId();
                discount = total.multiply(new BigDecimal(p.getDiscountPercent())).divide(new BigDecimal(100));
                finalTotal = total.subtract(discount);
                System.out.println("Áp dụng mã giảm " + p.getDiscountPercent() + "%. (Giảm " + discount + " VNĐ)");
            } else {
                System.out.println("Mã không hợp lệ hoặc đã hết hạn!");
            }
        }

        System.out.println("SỐ TIỀN THANH TOÁN SAU CÙNG: " + finalTotal + " VNĐ");
        System.out.println("1. Xác nhận đặt hàng | 0. Hủy");
        if ("1".equals(scanner.nextLine())) {
            Order order = new Order();
            order.setUserId(customer.getId());
            order.setTotalAmount(total);
            order.setDiscountAmount(discount);
            order.setFinalAmount(finalTotal);
            order.setStatus("PENDING");
            order.setShippingAddress(address);
            order.setPaymentMethod(method);
            order.setPromotionId(promoId);
            
            int orderId = orderDAO.createOrder(order, items);
            if (orderId > 0) {
                System.out.println(">>> ĐẶT HÀNG THÀNH CÔNG! Mã đơn: " + orderId);
                cartService.clearCart(customer.getId());
            } else {
                System.out.println("Lỗi hệ thống khi đặt hàng.");
            }
        }
    }

    private void orderHistory() {
        System.out.println("\n-- LỊCH SỬ ĐƠN HÀNG --");
        List<Order> orders = orderDAO.getOrdersByUser(customer.getId());
        if(orders.isEmpty()) {
            System.out.println("Chưa có đơn hàng nào.");
        }
        for (Order o : orders) {
            System.out.printf("[%s] Mã: %d | Tổng: %s | Trạng thái: %s | Địa chỉ: %s%n", o.getCreatedAt(), o.getId(), o.getFinalAmount(), o.getStatus(), o.getShippingAddress());
        }
    }
}
