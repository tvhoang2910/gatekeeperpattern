# Gatekeeper Design Pattern trong Spring Boot

Dự án này là một ví dụ minh họa về việc triển khai "Gatekeeper" Design Pattern để ngăn chặn các cuộc tấn công SQL Injection cơ bản trong một ứng dụng Spring Boot.

## Kiến trúc

Một `jakarta.servlet.Filter` có tên là `SqlInjectionGatekeeperFilter` được cấu hình để chặn tất cả các request đến `/api/**`. Filter này kiểm tra cả query parameters và request body để tìm các chuỗi ký tự đáng ngờ (ví dụ: `OR 1=1`, `--`, `;`). Nếu phát hiện mối đe dọa, request sẽ bị chặn với mã lỗi `HTTP 403 Forbidden`. Nếu không, request sẽ được chuyển tiếp đến controller.

## Cách chạy dự án

1.  Yêu cầu:
    *   JDK 21 hoặc mới hơn
    *   Maven 3.8+

2.  Chạy ứng dụng:
    ```bash
    mvn spring-boot:run
    ```
    Ứng dụng sẽ khởi động và lắng nghe trên cổng 8080.

## Hướng dẫn kiểm thử

Bạn có thể sử dụng cURL, Postman, hoặc bất kỳ công cụ API nào khác để kiểm thử.

### Kịch bản 1: Kiểm tra Query Parameter (GET /api/data)

#### 1.1. Request hợp lệ (sẽ thành công)

```bash
curl -X GET "http://localhost:8080/api/data?search=my_product"