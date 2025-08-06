# Quy tắc viết Service

## Nguyên tắc
- Logic chia rõ theo tầng
- Không viết SQL thủ công trong service
- Không gọi WebSocket hoặc Redis trong Controller trực tiếp

## Tổ chức
- Mỗi service tương ứng 1 chức năng: `RoomService`, `GameService`, `UserService`
- Không inject repository chéo lung tung

## Ví dụ
```java
public interface RoomService {
    RoomDto createRoom(CreateRoomRequest req);
    void startGame(Long roomId);
}
```
