# Quizizz Backend API

## Mô tả
Backend API cho ứng dụng Quizizz - hệ thống tạo và quản lý quiz trực tuyến.

## Công nghệ sử dụng
- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** - Authentication & Authorization
- **JWT** - Token-based authentication
- **Redis** - Caching & Session management
- **MySQL** - Database
- **Maven** - Build tool
- **Swagger/OpenAPI** - API Documentation

## Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- PostgreSQL
- Redis 6.0+

## Cài đặt và chạy

### 1. Clone repository
```bash
git clone <repository-url>
cd quizizz-backend
```

### 2. Cấu hình database
Tạo database MySQL:
```sql
CREATE DATABASE quizizz_db;
```

### 3. Cấu hình môi trường
Tạo file `.env` trong thư mục gốc:
```env
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=quizizz_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### 4. Chạy ứng dụng
```bash
# Sử dụng Maven
mvn spring-boot:run

# Hoặc build và chạy jar
mvn clean package
java -jar target/quizizz-0.0.1-SNAPSHOT.jar
```

### 5. Chạy với Docker
```bash
docker-compose up -d
```

## API Documentation

### Swagger UI
Truy cập: `http://localhost:8080/swagger-ui.html`

### Base URL
```
http://localhost:8080/api/v1
```

## Cấu trúc project
```
src/
├── main/
│   ├── java/com/example/quizizz/
│   │   ├── config/          # Cấu hình Spring
│   │   ├── controller/      # REST Controllers
│   │   ├── model/          # Entities & DTOs
│   │   ├── repository/     # Data Access Layer
│   │   ├── service/        # Business Logic
│   │   ├── security/       # Security & JWT
│   │   ├── enums/          # Enums
│   │   └── exception/      # Exception Handling
│   └── resources/
│       ├── application.yml
│       └── static/
└── test/                   # Unit Tests
```

## Authentication & Authorization

### JWT Token
- **Access Token**: Thời gian sống 24h
- **Refresh Token**: Thời gian sống 7 ngày
- **Header**: `Authorization: Bearer <token>`

### Permissions
- `user:manage_profile` - Quản lý profile cá nhân
- `permission:manage` - Quản lý permissions
- `user:manage` - Quản lý users và role-permission
- `ADMIN_FULL_ACCESS` - Toàn quyền admin

## API Endpoints

### Authentication
- `POST /auth/register` - Đăng ký
- `POST /auth/login` - Đăng nhập
- `POST /auth/logout` - Đăng xuất
- `POST /auth/refresh` - Refresh token
- `POST /auth/reset-password` - Reset mật khẩu

### Profile Management
- `GET /profile` - Lấy thông tin profile
- `PUT /profile` - Cập nhật profile
- `POST /profile/avatar` - Upload avatar
- `GET /profile/avatar` - Lấy URL avatar

### Permission Management
- `POST /permissions` - Tạo permission
- `GET /permissions` - Lấy danh sách permissions
- `PUT /permissions/{id}` - Cập nhật permission
- `DELETE /permissions/{id}` - Xóa permission

### Role Management
- `POST /roles` - Tạo role
- `GET /roles` - Lấy danh sách roles
- `PUT /roles/{id}` - Cập nhật role
- `DELETE /roles/{id}` - Xóa role

### Role-Permission Management
- `POST /role-permissions/assign-permissions-to-role` - Gán quyền cho role
- `DELETE /role-permissions/remove-permissions-from-role` - Xóa quyền khỏi role

### Room Management (REST API)
- `POST /rooms` - Tạo phòng (tự động join vào waiting room)
- `GET /rooms/{id}` - Lấy thông tin phòng
- `GET /rooms/code/{roomCode}` - Lấy phòng theo room code
- `POST /rooms/join` - Join phòng bằng room code
- `POST /rooms/{id}/join-direct` - Join phòng public bằng ID
- `DELETE /rooms/{id}/leave` - Rời phòng
- `GET /rooms/{id}/players` - Lấy danh sách players
- `GET /rooms/public` - Lấy danh sách phòng public
- `GET /rooms/my-rooms` - Lấy phòng của user

### Topic Management
- `GET /topics` - Lấy danh sách topics
- `POST /topics` - Tạo topic mới
- `PUT /topics/{id}` - Cập nhật topic
- `DELETE /topics/{id}` - Xóa topic

## Socket.IO Integration

### Connection Setup
```javascript
// Connect với JWT token
import io from 'socket.io-client';

const socket = io('http://localhost:9092', {
    query: {
        token: jwtToken
    },
    transports: ['websocket']
});

socket.on('connect', () => {
    console.log('Connected to Socket.IO server');
});
```

### Room Flow

#### 1. Tạo phòng (REST API)
```javascript
// POST /api/v1/rooms
const createRoom = async () => {
    const response = await fetch('/api/v1/rooms', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify({
            roomName: "My Room",
            roomMode: "BATTLE_ROYAL", // hoặc "ONE_VS_ONE"
            topicId: 1,
            isPrivate: false,
            maxPlayers: 10,
            questionCount: 10,
            countdownTime: 30
        })
    });
    
    const room = await response.json();
    // Tự động vào waiting room
    enterWaitingRoom(room.data.id);
};
```

#### 2. Vào Waiting Room (Socket.IO)
```javascript
const enterWaitingRoom = (roomCode) => {
    // Join room
    socket.emit('join-room', {
        roomCode: roomCode
    });
    
    // Listen for room events
    socket.on('player-joined', handlePlayerJoined);
    socket.on('player-left', handlePlayerLeft);
    socket.on('game-started', handleGameStarted);
    
    // Get current players
    socket.emit('get-players', { roomId: roomId });
};
```

#### 3. Handle Real-time Events
```javascript
// Player events
socket.on('player-joined', (data) => {
    console.log(`User ${data.userId} joined`);
    updatePlayerList();
});

socket.on('player-left', (data) => {
    console.log(`User ${data.userId} left`);
    updatePlayerList();
});

socket.on('player-kicked', (data) => {
    console.log(`Player ${data.kickedPlayerId} was kicked`);
    updatePlayerList();
});

socket.on('game-started', (data) => {
    console.log('Game started!');
    navigateToGame();
});
```

### Socket.IO Events

#### Player Actions
```javascript
// Rời phòng
socket.emit('leave-room', {
    roomId: roomId
});

// Lấy danh sách players
socket.emit('get-players', {
    roomId: roomId
});
```

#### Host Actions
```javascript
// Kick player
socket.emit('kick-player', {
    roomId: roomId,
    playerId: playerId,
    reason: "Reason for kick"
});

// Bắt đầu game
socket.emit('start-game', {
    roomId: roomId
});
```

### Socket.IO Event Listeners
- `player-joined` - Player joins room
- `player-left` - Player leaves room
- `player-kicked` - Player gets kicked
- `game-started` - Game begins
- `room-players` - Room players list
- `next-question` - Next question in game
- `answer-submitted` - Answer submission result
- `game-finished` - Game completion

### Game Events (After Start)
```javascript
// Listen for game events
socket.on('next-question', (data) => {
    displayQuestion(data.question);
});

socket.on('game-finished', (data) => {
    showResults(data.result);
});

socket.on('answer-submitted', (data) => {
    updateScoreboard(data);
});

// Submit answer
socket.emit('submit-answer', {
    roomId: roomId,
    questionId: questionId,
    answerId: answerId,
    timeTaken: timeTaken
});

// Next question (Host only)
socket.emit('next-question', {
    roomId: roomId
});
```

## Room States
- **WAITING** - Chờ players join
- **PLAYING** - Đang chơi game
- **FULL** - Đã đủ players
- **FINISHED** - Game đã kết thúc
- **ARCHIVED** - Phòng đã được lưu trữ

## Testing

### Chạy tests
```bash
mvn test
```

### Test với Postman
Import collection từ: `docs/postman/Quizizz-API.postman_collection.json`

## Deployment

### Production Build
```bash
mvn clean package -Pprod
```

### Docker Production
```bash
docker build -t quizizz-backend .
docker run -p 8080:8080 quizizz-backend
```

## Monitoring & Logging

### Health Check
```
GET /actuator/health
```

### Logs
Logs được lưu tại: `logs/application.log`

## Security Features

- **JWT Authentication** với blacklist token
- **Role-based Access Control (RBAC)**
- **Password encryption** với BCrypt
- **CORS configuration**
- **Rate limiting** (Redis-based)
- **Input validation** với Bean Validation

## Database Schema

### Core Tables
- `users` - Thông tin người dùng
- `roles` - Vai trò hệ thống
- `permissions` - Quyền hạn
- `role_permissions` - Mapping role-permission
- `user_roles` - Mapping user-role

## Error Handling

### Response Format
```json
{
  "success": false,
  "message": "ERROR_CODE",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Common Error Codes
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

## Contributing

### Code Style
- Sử dụng Google Java Style Guide
- Lombok để giảm boilerplate code
- Validation annotations cho input validation

### Git Workflow
1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push và tạo Pull Request

## Support

### Documentation
- [API Documentation](API_DOCUMENTATION.md)
- [Architecture Guidelines](rules/architecture.markdown)
- [Service Guidelines](rules/service-guidelines.markdown)

### WebSocket Guide
- [WebSocket Integration](WEBSOCKET_GUIDE.md)
- [Room Management Flow](docs/room-flow.md)

### Contact
- Email: support@quizizz.com
- Issues: GitHub Issues

## License
MIT License