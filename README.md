# Quan-Ly-ban-Dien-thoai
Chi tiết Kế hoạch (Proposed Changes)
Dự án sẽ được khởi tạo trong thư mục C:\btl (môi trường máy tính của bạn).
## 1. Cấu trúc thư mục Maven 
Sử dụng công cụ để khởi tạo pom.xml bao gồm thư viện mysql-connector-java.

## 2. Database (XAMPP / MySQL)
Tạo file database/schema.sql với các bảng cần thiết:
```sql
  users (id, username, password, role),
brands (id, name)
products (id, brand_id, name, price, stock, is_active)
reviews (id, user_id, product_id, comment, created_at)
orders (id, user_id, total_amount, status, created_at, shipping_address)
order_items (id, order_id, product_id, quantity, price)
promotions (id, code, discount_percent, is_active)
(Giỏ hàng có thể xử lý in-memory khi user đang thao tác, hoặc lưu vào database)
## 3. Phân chia thư mục mã nguồn (Packages)
Kiến trúc phần mềm sẽ bao gồm các tầng (Layer):

model: Chứa các lớp thực thể (User, Product, Brand, Order...)
dao (Data Access Object): Xử lý các câu lệnh SQL INSERT/UPDATE/SELECT...
service: Chứa logic nghiệp vụ (Kiểm tra đăng nhập, xác nhận mua hàng...)
view: Giao diện Console (Menu hiển thị chữ, nhận ký tự nhập từ Scanner), bao gồm Menu Admin và Menu Customer.
utils: File cấu hình kết nối Database (DBConnection).
## 4. Lộ trình Triển khai Code
Chúng ta sẽ cài đặt tất cả 12 usecase qua các Views: 
Admin Flow:

Quản lý Thương hiệu (Brands)
Quản lý Sản phẩm (Kho hàng)
Quản lý Đơn hàng (Duyệt đơn)
Quản lý Khuyến mãi (Tạo mã giảm giá)
Dashboard (Xem doanh thu, sản phẩm bán chạy)
Customer Flow:

Đăng ký / Đăng nhập
Khám phá (Tìm kiếm, xem sản phẩm, đánh giá)
Mua sắm (Thêm vào giỏ hàng, đặt hàng với địa chỉ)
Lịch sử mua hàng (Xem các đơn hàng đã đặt và trạng thái)

