package com.phonestore;

import com.phonestore.dao.UserDAO;
import com.phonestore.model.User;
import com.phonestore.view.AdminView;
import com.phonestore.view.CustomerView;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Fix encoding for Vietnamese output on Windows terminal
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
        } catch (Exception e) { /* ignore */ }
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        System.out.println("==================================================");
        System.out.println("   CHÀO MỪNG ĐẾN VỚI HỆ THỐNG QUẢN LÝ ĐIỆN THOẠI  ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- MENU TRANG CHỦ ---");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng ký khách hàng mới");
            System.out.println("3. Thoát hệ thống");
            System.out.print("Chọn chức năng: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleLogin(scanner, userDAO);
                    break;
                case "2":
                    handleRegister(scanner, userDAO);
                    break;
                case "3":
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống! Tạm biệt.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
            }
        }
    }

    private static void handleLogin(Scanner scanner, UserDAO userDAO) {
        System.out.print("Nhập tên đăng nhập: ");
        String username = scanner.nextLine();
        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();

        User user = userDAO.login(username, password);
        if (user != null) {
            System.out.println("\n=> Đăng nhập thành công! Xin chào, " + user.getFullName());
            if ("ADMIN".equals(user.getRole())) {
                new AdminView(scanner, user).showMenu();
            } else {
                new CustomerView(scanner, user).showMenu();
            }
        } else {
            System.out.println("=> Sai tên đăng nhập hoặc mật khẩu.");
        }
    }

    private static void handleRegister(Scanner scanner, UserDAO userDAO) {
        System.out.println("\n--- ĐĂNG KÝ TÀI KHOẢN KHÁCH HÀNG ---");
        System.out.print("Tên đăng nhập mới: ");
        String username = scanner.nextLine();
        System.out.print("Mật khẩu: ");
        String password = scanner.nextLine();
        System.out.print("Họ và tên: ");
        String fullname = scanner.nextLine();
        System.out.print("Số điện thoại: ");
        String phone = scanner.nextLine();
        System.out.print("Địa chỉ: ");
        String address = scanner.nextLine();

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullname);
        newUser.setPhone(phone);
        newUser.setAddress(address);

        if (userDAO.register(newUser)) {
            System.out.println("=> Đăng ký thành công! Hãy quay lại đăng nhập.");
        } else {
            System.out.println("=> Đăng ký thất bại.");
        }
    }
}
