package com.phonestore.view;

import com.phonestore.dao.*;
import com.phonestore.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class AdminView {
    private Scanner scanner;
    private User admin;
    private BrandDAO brandDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private PromotionDAO promotionDAO;
    private ReviewDAO reviewDAO;
    private DashboardDAO dashboardDAO;

    public AdminView(Scanner scanner, User admin) {
        this.scanner = scanner;
        this.admin = admin;
        this.brandDAO = new BrandDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.promotionDAO = new PromotionDAO();
        this.reviewDAO = new ReviewDAO();
        this.dashboardDAO = new DashboardDAO();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN ===");
            System.out.println("1. Quản lý Hãng sản xuất");
            System.out.println("2. Quản lý Kho hàng");
            System.out.println("3. Quản lý Đơn hàng");
            System.out.println("4. Quản lý Khuyến mãi");
            System.out.println("5. Bảng thống kê");
            System.out.println("6. Xử lý đánh giá");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn chức năng: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    manageBrands();
                    break;
                case "2":
                    manageProducts();
                    break;
                case "3":
                    manageOrders();
                    break;
                case "4":
                    managePromotions();
                    break;
                case "5":
                    showDashboard();
                    break;
                case "6":
                    manageReviews();
                    break;
                case "0":
                    System.out.println("Đã đăng xuất khỏi tài khoản admin.");
                    return; // Về màn hình đăng nhập
                default:
                    System.out.println("Chức năng không hợp lệ!");
            }
        }
    }

    private void manageBrands() {
        System.out.println("\n-- QUẢN LÝ HÃNG --");
        List<Brand> brands = brandDAO.getAllBrands();
        for (Brand b : brands) {
            System.out.println(b);
        }
        System.out.println("1. Thêm mới | 2. Sửa | 3. Xóa | 0. Quay lại");
        System.out.print("Chọn: ");
        String c = scanner.nextLine();
        switch (c) {
            case "1":
                System.out.print("Tên hãng: ");
                String name = scanner.nextLine();
                System.out.print("Mô tả: ");
                String desc = scanner.nextLine();
                Brand nb = new Brand();
                nb.setName(name);
                nb.setDescription(desc);
                if (brandDAO.addBrand(nb))
                    System.out.println("Thêm thành công.");
                else
                    System.out.println("Thêm thất bại.");
                break;
            case "2":
                System.out.print("Nhập ID hãng cần sửa: ");
                int idUpdate = Integer.parseInt(scanner.nextLine());
                System.out.print("Tên mới: ");
                String nName = scanner.nextLine();
                System.out.print("Mô tả mới: ");
                String nDesc = scanner.nextLine();
                Brand ub = new Brand();
                ub.setId(idUpdate);
                ub.setName(nName);
                ub.setDescription(nDesc);
                if (brandDAO.updateBrand(ub))
                    System.out.println("Sửa thành công.");
                else
                    System.out.println("Sửa thất bại.");
                break;
            case "3":
                System.out.print("Nhập ID hãng cần xóa: ");
                int idDel = Integer.parseInt(scanner.nextLine());
                if (brandDAO.deleteBrand(idDel))
                    System.out.println("Xóa thành công.");
                else
                    System.out.println("Xóa thất bại.");
                break;
            case "0":
                return;
        }
    }

    private void manageProducts() {
        System.out.println("\n-- QUẢN LÝ KHO HÀNG --");
        List<Product> products = productDAO.getAllProductsAdmin();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.printf("[%d] %s - Giá: %s - Tồn: %d - Ẩn/Hiện: %b%n", i + 1, p.getName(), p.getPrice(),
                    p.getStock(), p.isActive());
        }
        System.out.println("1. Thêm sản phẩm | 2. Cập nhật SP | 0. Quay lại");
        System.out.print("Chọn: ");
        String c = scanner.nextLine();

        if ("1".equals(c)) {
            try {
                System.out.println("--- Danh sách Hãng khả dụng ---");
                List<Brand> brands = brandDAO.getAllBrands();
                for (Brand b : brands) {
                    System.out.println("ID: " + b.getId() + " - " + b.getName());
                }
                System.out.print("Vui lòng nhập đúng ID Hãng có trong danh sách trên: ");
                int bId = Integer.parseInt(scanner.nextLine());
                System.out.print("Tên sản phẩm: ");
                String name = scanner.nextLine();
                System.out.print("Giá bán: ");
                BigDecimal price = new BigDecimal(scanner.nextLine());
                System.out.print("Số lượng tồn: ");
                int stock = Integer.parseInt(scanner.nextLine());
                System.out.print("Cấu hình (Specs): ");
                String specs = scanner.nextLine();
                System.out.print("Kích hoạt (true/false): ");
                boolean active = Boolean.parseBoolean(scanner.nextLine());
                Product p = new Product(0, bId, name, price, stock, specs, active, null);
                if (productDAO.addProduct(p))
                    System.out.println("Đã thêm sản phẩm thành công!");
                else
                    System.out.println("Thêm sản phẩm thất bại.");
            } catch (Exception e) {
                System.out.println("Nhập sai định dạng dữ liệu.");
            }
        } else if ("2".equals(c)) {
            System.out.print("Nhập STT sản phẩm cần cập nhật (hoặc 0 để quay lại): ");
            try {
                int stt = Integer.parseInt(scanner.nextLine());
                if (stt == 0)
                    return;

                if (stt >= 1 && stt <= products.size()) {
                    Product existing = products.get(stt - 1);
                    boolean isUpdating = true;
                    while (isUpdating) {
                        System.out.println("\n--- ĐANG CHỈNH SỬA: [" + existing.getName() + "] ---");
                        System.out.println("1. Sửa Tên SP: " + existing.getName());
                        System.out.println("2. Sửa Giá bán: " + existing.getPrice());
                        System.out.println("3. Sửa Tồn kho: " + existing.getStock());
                        System.out.println("4. Sửa Cấu hình (Specs): " + existing.getSpecs());
                        System.out.println("5. Đổi ID Hãng: "
                                + (existing.getBrandName() != null ? existing.getBrandName() : existing.getBrandId()));
                        System.out.println("6. Ẩn/Hiện SP: " + existing.isActive());
                        System.out.println("9. [LƯU THAY ĐỔI VÀ THOÁT]");
                        System.out.println("0. [HỦY BỎ TẤT CẢ VÀ THOÁT]");
                        System.out.print("Chọn mục cần sửa: ");
                        String op = scanner.nextLine();

                        try {
                            switch (op) {
                                case "1":
                                    System.out.print("Nhập tên mới: ");
                                    String n = scanner.nextLine();
                                    if (!n.trim().isEmpty())
                                        existing.setName(n);
                                    break;
                                case "2":
                                    System.out.print("Nhập giá mới (VNĐ): ");
                                    existing.setPrice(new BigDecimal(scanner.nextLine()));
                                    break;
                                case "3":
                                    System.out.print("Nhập tồn kho mới: ");
                                    existing.setStock(Integer.parseInt(scanner.nextLine()));
                                    break;
                                case "4":
                                    System.out.print("Cấu hình (Specs) mới: ");
                                    existing.setSpecs(scanner.nextLine());
                                    break;
                                case "5":
                                    System.out.print("Nhập ID Hãng (Brand ID): ");
                                    existing.setBrandId(Integer.parseInt(scanner.nextLine()));
                                    break;
                                case "6":
                                    System.out.print("Trạng thái hiển thị (true/false): ");
                                    existing.setActive(Boolean.parseBoolean(scanner.nextLine()));
                                    break;
                                case "9":
                                    if (productDAO.updateProduct(existing)) {
                                        System.out.println(">>> Đã lưu mọi thay đổi thành công!");
                                    } else {
                                        System.out.println("Lỗi khi cập nhật database.");
                                    }
                                    isUpdating = false;
                                    break;
                                case "0":
                                    System.out.println("Đã hủy bỏ toàn bộ chỉnh sửa.");
                                    isUpdating = false;
                                    break;
                                default:
                                    System.out.println("Lựa chọn không hợp lệ.");
                            }
                        } catch (Exception ex) {
                            System.out.println("=> Lỗi: Nhập sai kiểu dữ liệu! Hãy nhập lại cho đúng.");
                        }
                    }
                } else {
                    System.out.println("Không tìm thấy sản phẩm.");
                }
            } catch (Exception e) {
                System.out.println("Nhập sai định dạng mã ID.");
            }
        }
    }

    private String getStatusVN(String enStatus) {
        if (enStatus == null)
            return "Không xác định";
        switch (enStatus.toUpperCase()) {
            case "PENDING":
                return "Chờ duyệt";
            case "APPROVED":
                return "Đã duyệt";
            case "SHIPPED":
                return "Đang giao";
            case "DELIVERED":
                return "Đã giao";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return enStatus;
        }
    }

    private void manageOrders() {
        while (true) {
            System.out.println("\n-- QUẢN LÝ ĐƠN HÀNG --");
            List<Order> orders = orderDAO.getAllOrders();
            for (Order o : orders) {
                System.out.printf("Mã ĐH: %d | UserID: %d | Tổng: %s | Trạng thái: %s | Ngày: %s%n", o.getId(),
                        o.getUserId(), o.getFinalAmount(), getStatusVN(o.getStatus()), o.getCreatedAt());
                List<OrderItem> items = orderDAO.getOrderItemsByOrderId(o.getId());
                for (OrderItem item : items) {
                    System.out.printf("   -> SẢN PHẨM: %s | Số lượng: %d | Đơn giá: %s VNĐ%n", item.getProductName(),
                            item.getQuantity(), item.getPrice());
                }
            }
            System.out.println("\n1. Duyệt đơn hàng đang chờ duyệt");
            System.out.println("2. Cập nhật trạng thái giao hàng (ĐANG GIAO / ĐÃ GIAO)");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine();

            if ("1".equals(choice)) {
                System.out.print("Nhập mã đơn cần duyệt: ");
                try {
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Quyết định: Chấp nhận (A) / Hủy bỏ (C): ");
                    String decision = scanner.nextLine().trim().toUpperCase();
                    String status = decision.equals("A") ? "APPROVED" : (decision.equals("C") ? "CANCELLED" : "");

                    if (!status.isEmpty() && orderDAO.updateOrderStatus(id, status)) {
                        System.out.println("Đã duyệt đơn thành công!");
                    } else {
                        System.out.println("Duyệt đơn thất bại hoặc lựa chọn không hợp lệ.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(">> Vui lòng nhập số hợp lệ.");
                }
            } else if ("2".equals(choice)) {
                System.out.print("Nhập mã đơn cần cập nhật: ");
                try {
                    int id = Integer.parseInt(scanner.nextLine());

                    Order targetOrder = null;
                    for (Order o : orders) {
                        if (o.getId() == id) {
                            targetOrder = o;
                            break;
                        }
                    }

                    if (targetOrder == null) {
                        System.out.println("Không tìm thấy mã đơn hàng này!");
                    } else if ("PENDING".equals(targetOrder.getStatus())) {
                        System.out.println(
                                "LỖI: Đơn hàng này CHƯA ĐƯỢC DUYỆT! Bạn phải duyệt đơn trước khi giao hàng.");
                    } else if ("CANCELLED".equals(targetOrder.getStatus())) {
                        System.out.println("LỖI: Đơn hàng này đã bị HỦY BỎ, không thể giao hàng.");
                    } else {
                        System.out.print("Trạng thái mới giao hàng (ĐANG GIAO / ĐÃ GIAO): ");
                        String inputStatus = scanner.nextLine().trim().toUpperCase();
                        String status = "";
                        if (inputStatus.equals("ĐANG GIAO") || inputStatus.equals("DANG GIAO")) {
                            status = "SHIPPED";
                        } else if (inputStatus.equals("ĐÃ GIAO") || inputStatus.equals("DA GIAO")) {
                            status = "DELIVERED";
                        }

                        if (!status.isEmpty()) {
                            if (orderDAO.updateOrderStatus(id, status)) {
                                System.out.println("Cập nhật giao hàng thành công!");
                            } else {
                                System.out.println("Cập nhật thất bại.");
                            }
                        } else {
                            System.out.println("Trạng thái không hợp lệ! Vui lòng nhập ĐANG GIAO hoặc ĐÃ GIAO.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println(">> Vui lòng nhập số hợp lệ.");
                }
            } else if ("0".equals(choice)) {
                return;
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }

    private void managePromotions() {
        System.out.println("\n-- QUẢN LÝ KHUYẾN MÃI --");
        List<Promotion> promos = promotionDAO.getAllPromotions();
        for (Promotion p : promos) {
            System.out.printf("Mã KM: %s | Giảm: %d%% | Trạng thái: %b%n", p.getCode(), p.getDiscountPercent(),
                    p.isActive());
        }
        System.out.println("1. Tạo mã mới | 0. Quay lại");
        System.out.print("Chọn: ");
        if ("1".equals(scanner.nextLine())) {
            System.out.print("Nhập mã (VD: TET2025): ");
            String code = scanner.nextLine();
            System.out.print("Phần trăm giảm (1-100): ");
            int percent = Integer.parseInt(scanner.nextLine());
            Promotion p = new Promotion();
            p.setCode(code);
            p.setDiscountPercent(percent);
            p.setActive(true);
            if (promotionDAO.addPromotion(p))
                System.out.println("Tạo mã KM thành công.");
            else
                System.out.println("Tạo mã lỗi.");
        }
    }

    private void showDashboard() {
        System.out.println("\n-- BẢNG THỐNG KÊ (DASHBOARD) --");
        Dashboard stats = dashboardDAO.getDashboardStats();

        System.out.println("Tổng doanh thu: " + stats.getTotalRevenue() + " VNĐ");
        System.out.println("Số đơn hàng đã hoàn thành: " + stats.getCompletedOrders());
        System.out.println("Số đơn đang chờ duyệt: " + stats.getPendingOrders());

        System.out.println("\n--- TOP ĐIỆN THOẠI BÁN CHẠY NHẤT ---");
        List<String> topSelling = stats.getTopSellingProducts();
        if (topSelling.isEmpty()) {
            System.out.println("Chưa có dữ liệu giao hàng thành công.");
        } else {
            for (int i = 0; i < topSelling.size(); i++) {
                System.out.println((i + 1) + ". " + topSelling.get(i));
            }
        }

        System.out.println("\nNhấn Enter để quay lại...");
        scanner.nextLine();
    }

    private void manageReviews() {
        System.out.println("\n-- XỬ LÝ ĐÁNH GIÁ CỦA KHÁCH HÀNG --");
        List<Review> reviews = reviewDAO.getAllReviews();
        if (reviews.isEmpty()) {
            System.out.println("Chưa có đánh giá nào.");
        } else {
            for (Review r : reviews) {
                System.out.printf("[%d] SP: %s | KH: %s | Đánh giá: %d sao | Nội dung: %s%n", r.getId(),
                        r.getProductName(), r.getUserName(), r.getRating(), r.getComment());
                if (r.getAdminReply() != null && !r.getAdminReply().isEmpty()) {
                    System.out.println("    => Lời phản hồi: " + r.getAdminReply());
                }
            }
        }
        System.out.println("1. Trả lời đánh giá | 0. Quay lại");
        System.out.print("Chọn: ");
        if ("1".equals(scanner.nextLine())) {
            System.out.print("Nhập ID đánh giá cần trả lời: ");
            int rId = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập nội dung phản hồi: ");
            String reply = scanner.nextLine();
            if (reviewDAO.replyReview(rId, reply)) {
                System.out.println("Phản hồi thành công!");
            } else {
                System.out.println("Phản hồi thất bại.");
            }
        }
    }
}
