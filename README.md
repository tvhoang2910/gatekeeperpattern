# Gatekeeper Pattern – Chống SQL Injection

## Cấu trúc thư mục

```
gatekeeper-pattern/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── gatekeeperpattern/
        │               ├── GatekeeperPatternApplication.java
        │               ├── config/
        │               │   └── FilterConfig.java
        │               ├── controller/
        │               │   └── ApiDataController.java
        │               └── filter/
        │                   ├── CachedBodyHttpServletRequest.java
        │                   └── SqlInjectionGatekeeperFilter.java
        └── resources/
            └── application.yml
```

## Giải thích code

- **`SqlInjectionGatekeeperFilter`**: Filter chặn mọi request đến `/api/*`. Kiểm tra query parameters và JSON body để phát hiện các mẫu SQL injection như `'`, `--`, `UNION`, `SELECT`, `OR 1=1`, v.v. Nếu phát hiện, trả về **HTTP 403** với JSON lỗi.
- **`CachedBodyHttpServletRequest`**: Wrapper cho `HttpServletRequest` để đọc body nhiều lần — cần thiết vì Filter và Spring đều cần truy cập body.
- **`FilterConfig`**: Đăng ký Filter với URL pattern `/api/*` và thứ tự ưu tiên cao (`order = 1`).
- **`ApiDataController`**: Controller mẫu với 2 endpoint:
  - `GET /api/data?search=...`
  - `POST /api/users` (nhận JSON body)

## Cách chạy

```bash
mvn spring-boot:run
```

Ứng dụng chạy trên: `http://localhost:8080`

## Kiểm thử bằng cURL

### 1. GET hợp lệ

```bash
curl -X GET "http://localhost:8080/api/data?search=product123"
```

→ Trả về HTTP 200 và JSON thành công.

### 2. GET bị chặn (SQL Injection)

```bash
curl -X GET "http://localhost:8080/api/data?search=1' OR '1'='1"
```

→ Trả về HTTP 403:
```json
{"error": "Forbidden", "message": "Potential SQL Injection detected in query parameter."}
```

### 3. POST hợp lệ

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secure"}'
```

→ Trả về HTTP 200 và JSON thành công.

### 4. POST bị chặn (SQL Injection)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"pass' OR 1=1--"}'
```

→ Trả về HTTP 403:
```json
{"error": "Forbidden", "message": "Potential SQL Injection detected in request body."}
```