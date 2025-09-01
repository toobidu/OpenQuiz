# Quy tắc viết API

## Đặt tên
- Dùng `snake_case` cho URL, `camelCase` cho JSON field.
- Bắt đầu với `/api/v1/`.

## RESTful routes ví dụ
- `POST /api/v1/auth/login` – Đăng nhập, trả về JWT.
- `GET /api/v1/users/{id}` – Lấy thông tin user.
- `POST /api/v1/rooms` – Tạo phòng.
- `POST /api/v1/rooms/{id}/start` – Host bắt đầu game.
- `GET /api/v1/rooms/{id}/players` – Lấy danh sách người chơi.
- `POST /api/v1/recommend` – Gọi Python backend để lấy gợi ý người chơi.

## Trả về chuẩn
- Thành công:
  ```json
  {
    "status": "success",
    "message": "Thành công",
    "data": { ... }
  }
  ```
- Lỗi:
  ```json
  {
    "status": "error",
    "message": "Invalid input",
    "error_code": "INVALID_INPUT",
    "details": { ... }
  }
  ```

## WebSocket (STOMP)
- Kết nối: `/ws`
- Subscribe: `/topic/rooms/{roomId}` (nhận questions, scores).
- Gửi message: `/app/rooms/{roomId}/answer` (gửi đáp án).

## Tích hợp với Python
- Endpoint: `POST /api/v1/recommend`
- Payload:
  ```json
  {
    "user_id": 1,
    "preferences": [0.8, 0.5, 0.2]
  }
  ```
- Response từ Python:
  ```json
  {
    "status": "success",
    "data": {
      "recommended_users": [2, 5, 7]
    }
  }
  ```

## Error Handling
- 400: Bad Request (input không hợp lệ).
- 401: Unauthorized (JWT hết hạn hoặc sai).
- 404: Not Found (room_id/user_id không tồn tại).
- 500: Server Error (lỗi hệ thống, ghi log chi tiết).