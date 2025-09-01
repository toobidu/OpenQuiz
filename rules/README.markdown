# Quizizz Backend (Spring Boot)
Dự án backend cho game kiểu Quizizz, sử dụng Java Spring Boot, WebSocket, PostgreSQL, Redis.

## Tính năng chính
- Tạo/Tham gia phòng chơi
- Chơi game trả lời câu hỏi 4 đáp án
- Tính điểm theo tốc độ + độ chính xác
- Thống kê, xếp hạng người chơi
- Gợi ý người chơi tương đồng (cosine similarity)
- Hệ thống role/permission quản trị

## Công nghệ sử dụng
- Java 21 + Spring Boot
- Spring Web + Spring Security + WebSocket
- PostgreSQL + Redis
- JPA/Hibernate 
- JWT Auth
- Python FastAPI (recommendation microservice)
- ReactJS (frontend integration)

## Cài đặt
1. Clone repo: `git clone <repo-url>`
2. Cài đặt PostgreSQL, Redis
3. Cấu hình `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/quizizz
   spring.datasource.username=postgres
   spring.datasource.password=secret
   spring.redis.host=localhost
   spring.redis.port=6379
   ```
4. Chạy: `mvn spring-boot:run`

## Tích hợp
- **Frontend**: ReactJS gọi `/api/v1/` và WebSocket `/ws`.
- **Recommendation**: Gọi Python FastAPI (`POST /recommend`) để lấy gợi ý người chơi.