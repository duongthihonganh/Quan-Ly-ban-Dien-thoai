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

    public AdminView(Scanner scanner, User admin) {
        this.scanner = scanner;
        this.admin = admin;
        this.brandDAO = new BrandDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.promotionDAO = new PromotionDAO();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN ===");
            System.out.println("1. Quản lý Hãng sản xuất");
            System.out.println("2. Quản lý Sản phẩm (Kho hàng)");
            System.out.println("3. Quản lý Đơn hàng");
            System.out.println("4. Quản lý Khuyến mãi");
            System.out.println("5. Bảng thống kê (Dashboard)");
            System.out.println("0. Đăng xuất");
            System.out.print("Chọn chức năng: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": manageBrands(); break;
                case "2": manageProducts(); break;
                case "3": manageOrders(); break;
                case "4": managePromotions(); break;
                case "5": showDashboard(); break;
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
                System.out.print("Tên hãng: "); String name = scanner.nextLine();
                System.out.print("Mô tả: "); String desc = scanner.nextLine();
                Brand nb = new Brand(); nb.setName(name); nb.setDescription(desc);
                if (brandDAO.addBrand(nb)) System.out.println("Thêm thành công.");
                else System.out.println("Thêm thất bại.");
                break;
            case "2":
                System.out.print("Nhập ID hãng cần sửa: "); int idUpdate = Integer.parseInt(scanner.nextLine());
                System.out.print("Tên mới: "); String nName = scanner.nextLine();
                System.out.print("Mô tả mới: "); String nDesc = scanner.nextLine();
                Brand ub = new Brand(); ub.setId(idUpdate); ub.setName(nName); ub.setDescription(nDesc);
                if (brandDAO.updateBrand(ub)) System.out.println("Sửa thành công.");
                else System.out.println("Sửa thất bại.");
                break;
            case "3":
                System.out.print("Nhập ID hãng cần xóa: "); int idDel = Integer.parseInt(scanner.nextLine());
                if (brandDAO.deleteBrand(idDel)) System.out.println("Xóa thành công.");
                else System.out.println("Xóa thất bại.");
                break;
            case "0": return;
        }
    }

    private void manageProducts() {
        System.out.println("\n-- QUẢN LÝ KHO HÀNG --");
        List<Product> products = productDAO.getAllProductsAdmin();
        for (Product p : products) {
            System.out.printf("[%d] %s - Giá: %s - Tồn: %d - Ẩn/Hiện: %b%n", p.getId(), p.getName(), p.getPrice(), p.getStock(), p.isActive());
        }
        System.out.println("1. Thêm sản phẩm | 2. Cập nhật SP | 0. Quay lại");
        System.out.print("Chọn: ");
        String c = scanner.nextLine();
        
        if ("1".equals(c)) {
            try {
                System.out.print("ID Hãng: "); int bId = Integer.parseInt(scanner.nextLine());
                System.out.print("Tên sản phẩm: "); String name = scanner.nextLine();
                System.out.print("Giá bán: "); BigDecimal price = new BigDecimal(scanner.nextLine());
                System.out.print("Số lượng tồn: "); int stock = Integer.parseInt(scanner.nextLine());
                System.out.print("Cấu hình (Specs): "); String specs = scanner.nextLine();
                System.out.print("Kích hoạt (true/false): "); boolean active = Boolean.parseBoolean(scanner.nextLine());
                Product p = new Product(0, bId, name, price, stock, specs, active, null);
                if (productDAO.addProduct(p)) System.out.println("Đã thêm sản phẩm thành công!");
                else System.out.println("Thêm sản phẩm thất bại.");
            } catch (Exception e) {
                System.out.println("Nhập sai định dạng dữ liệu.");
            }
        } else if ("2".equals(c)) {
            System.out.print("Nhập ID sản phẩm cần cập nhật: "); 
            try {
                int id = Integer.parseInt(scanner.nextLine());
                Product existing = productDAO.getProductById(id);
                if (existing != null) {
                    System.out.print("Giá bán mới (cũ: " + existing.getPrice() + "): "); 
                    BigDecimal price = new BigDecimal(scanner.nextLine());
                    System.out.print("Tồn kho mới (cũ: " + existing.getStock() + "): "); 
                    int stock = Integer.parseInt(scanner.nextLine());
                    System.out.print("Hiển thị (true/false) (cũ: " + existing.isActive() + "): "); 
                    boolean active = Boolean.parseBoolean(scanner.nextLine());

                    existing.setPrice(price);
                    existing.setStock(stock);
                    existing.setActive(active);
                    if (productDAO.updateProduct(existing)) System.out.println("Cập nhật thành công!");
                    else System.out.println("Cập nhật thất bại.");
                } else {
                    System.out.println("Không tìm thấy sản phẩm.");
                }
            } catch (Exception e) {
                System.out.println("Nhập sai định dạng.");
            }
        }
    }

    private void manageOrders() {
        System.out.println("\n-- QUẢN LÝ ĐƠN HÀNG --");
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) {
            System.out.printf("Mã ĐH: %d | UserID: %d | Tổng: %s | Trạng thái: %s | Hóa đơn: %s%n", o.getId(), o.getUserId(), o.getFinalAmount(), o.getStatus(), o.getCreatedAt());
        }
        System.out.println("1. Cập nhật trạng thái (PENDING -> SHIPPED -> DELIVERED) | 0. Quay lại");
        System.out.print("Chọn: ");
        if ("1".equals(scanner.nextLine())) {
            System.out.print("Mã đơn cần cập nhật: "); 
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Trạng thái mới (SHIPPED / DELIVERED / CANCELLED): "); 
            String status = scanner.nextLine();
            if (orderDAO.updateOrderStatus(id, status)) {
                System.out.println("Cập nhật thành công!");
            } else {
                System.out.println("Cập nhật thất bại.");
            }
        }
    }

    private void managePromotions() {
        System.out.println("\n-- QUẢN LÝ KHUYẾN MÃI --");
        List<Promotion> promos = promotionDAO.getAllPromotions();
        for (Promotion p : promos) {
            System.out.printf("Mã KM: %s | Giảm: %d%% | Trạng thái: %b%n", p.getCode(), p.getDiscountPercent(), p.isActive());
        }
        System.out.println("1. Tạo mã mới | 0. Quay lại");
        System.out.print("Chọn: ");
        if ("1".equals(scanner.nextLine())) {
            System.out.print("Nhập mã (VD: TET2025): "); String code = scanner.nextLine();
            System.out.print("Phần trăm giảm (1-100): "); int percent = Integer.parseInt(scanner.nextLine());
            Promotion p = new Promotion(); p.setCode(code); p.setDiscountPercent(percent); p.setActive(true);
            if (promotionDAO.addPromotion(p)) System.out.println("Tạo mã KM thành công.");
            else System.out.println("Tạo mã lỗi.");
        }
    }

    private void showDashboard() {
        System.out.println("\n-- BẢNG THỐNG KÊ (DASHBOARD) --");
        // Simplified dashboard querying via standard orders
        List<Order> orders = orderDAO.getAllOrders();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int completedOrders = 0;
        int currentPending = 0;

        for (Order o : orders) {
            if ("DELIVERED".equals(o.getStatus())) {
                totalRevenue = totalRevenue.add(o.getFinalAmount());
                completedOrders++;
            } else if ("PENDING".equals(o.getStatus())) {
                currentPending++;
            }
        }

        System.out.println("Tổng doanh thu (Đã giao hàng): " + totalRevenue + " VNĐ");
        System.out.println("Số đơn hàng đã hoàn thành: " + completedOrders);
        System.out.println("Số đơn đang chờ duyệt: " + currentPending);
        System.out.println("Nhấn Enter để quay lại...");
        scanner.nextLine();
    }
}
