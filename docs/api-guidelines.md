# Quy tắc viết API

## Đặt tên
- Dùng `snake_case` cho URL, `camelCase` cho JSON field
- Bắt đầu với `/api/v1/`

## RESTful routes ví dụ
- `POST /api/v1/auth/login` – đăng nhập
- `GET /api/v1/users/{id}` – lấy thông tin user
- `POST /api/v1/rooms` – tạo phòng
- `POST /api/v1/rooms/{id}/start` – host bắt đầu game
- `GET /api/v1/rooms/{id}/players` – lấy danh sách người chơi

## Trả về chuẩn
```json
{
  "status": "success",
  "message": "Thành công",
  "data": { ... }
}
```

## WebSocket (STOMP)
- Kết nối: `/ws`
- Subscribe: `/topic/rooms/{roomId}`
- Gửi message: `/app/rooms/{roomId}/answer`
