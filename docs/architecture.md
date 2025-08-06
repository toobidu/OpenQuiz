# Kiến trúc hệ thống

## Thư mục chính
- `controller/` - Expose REST API / WebSocket endpoint
- `service/` - Xử lý logic nghiệp vụ
- `repository/` - Giao tiếp DB (JPA)
- `model/`
  - `entity/` - Entity ánh xạ JPA
  - `dto/` - Object truyền qua API
- `config/` - Cấu hình bảo mật, websocket, redis
- `event/` - Lưu xử lý event (room_events)
- `utils/` - Hàm tiện ích

## Luồng xử lý
1. REST API/WebSocket nhận request
2. Gọi `service` để xử lý logic
3. Service thao tác với DB qua `repository`
4. Kết quả trả về dưới dạng DTO
