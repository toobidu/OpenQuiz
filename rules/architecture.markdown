# Kiến trúc hệ thống

## Tổng quan

Hệ thống bao gồm 3 thành phần:

- **Backend Java Spring Boot**: Xử lý logic chính, REST API, WebSocket, lưu trữ dữ liệu (PostgreSQL, Redis).
- **Backend Python FastAPI**: Microservice xử lý recommendation (cosine similarity).
- **Frontend ReactJS**: Giao diện người dùng, gọi API và WebSocket.

## Thư mục chính (Spring Boot)

- `controller/` - Expose REST API / WebSocket endpoint
- `service/` - Xử lý logic nghiệp vụ
- `repository/` - Giao tiếp DB (JPA)
- `model/`
  - `entity/` - Entity ánh xạ JPA
  - `dto/` - Object truyền qua API
- `config/` - Cấu hình bảo mật, WebSocket, Redis
- `event/` - Lưu xử lý event (room_events)
- `utils/` - Hàm tiện ích (e.g., JWT parsing, vector conversion)

## Luồng xử lý

1. **REST API/WebSocket**:
   - Nhận request từ frontend ReactJS.
   - Gọi `service` để xử lý logic.
   - Service thao tác với DB qua `repository` hoặc gọi Python backend qua HTTP.
   - Kết quả trả về dưới dạng DTO.
2. **Recommendation**:
   - Spring Boot gửi user_id và preferences vector đến Python FastAPI (POST /recommend).
   - Python trả về danh sách user_ids tương đồng (cosine similarity).
3. **Real-time**:
   - WebSocket STOMP qua `/ws`, subscribe `/topic/rooms/{roomId}`.
   - Frontend nhận updates từ Spring Boot (scores, questions).

## Tích hợp

- **Spring Boot -&gt; Python**: Gọi API FastAPI (e.g., POST http://python-service/recommend) với JSON payload:

  ```json
  {
    "user_id": 1,
    "preferences": [0.8, 0.5, 0.2]
  }
  ```

  Response:

  ```json
  {
    "status": "success",
    "data": {
      "recommended_users": [2, 5, 7]
    }
  }
  ```
- **ReactJS -&gt; Spring Boot**: Gọi REST API (axios/fetch) với Authorization header (Bearer JWT). WebSocket qua SockJS + STOMP.
- **Database**: PostgreSQL (chung cho Spring Boot và Python), Redis cho caching room state.

## Diagram

- \[TBD: Thêm UML/Architecture diagram\]