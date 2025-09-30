# API Documentation - Quizizz Backend

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Tất cả API (trừ auth endpoints) yêu cầu JWT token trong header:
```
Authorization: Bearer <your_jwt_token>
```

## Common Response Format
```json
{
  "success": true,
  "message": "SUCCESS",
  "data": <response_data>,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

## 1. Authentication APIs

### 1.1 Register
**POST** `/auth/register`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "fullName": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "USER_CREATED",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "fullName": "string"
  }
}
```

### 1.2 Login
**POST** `/auth/login`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "AUTH_LOGIN_SUCCESS",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 1.3 Logout
**POST** `/auth/logout`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "AUTH_LOGOUT_SUCCESS",
  "data": "Logout successful"
}
```

### 1.4 Refresh Token
**POST** `/auth/refresh`

**Request Body:**
```json
"refresh_token_string"
```

**Response:**
```json
{
  "success": true,
  "message": "AUTH_TOKEN_REFRESHED",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 1.5 Reset Password
**POST** `/auth/reset-password`

**Request Body:**
```json
{
  "email": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "AUTH_PASSWORD_RESET_SUCCESS",
  "data": {
    "message": "Password reset successfully"
  }
}
```

---

## 2. Profile APIs

### 2.1 Get Profile
**GET** `/profile`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "SUCCESS",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatarUrl": "string"
  }
}
```

### 2.2 Update Profile
**PUT** `/profile`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "fullName": "string",
  "email": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "SUCCESS",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatarUrl": "string"
  }
}
```

### 2.3 Update Avatar
**POST** `/profile/avatar`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Request Body (Form Data):**
```
file: <image_file> (max 5MB, image types only)
```

**Response:**
```json
{
  "success": true,
  "message": "AVATAR_UPDATED",
  "data": {
    "avatarUrl": "string"
  }
}
```

### 2.4 Get Avatar URL
**GET** `/profile/avatar`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "AVATAR_URL_RETRIEVED",
  "data": "avatar_url_string"
}
```

---

## 3. Permission APIs
*Requires: `permission:manage` authority*

### 3.1 Create Permission
**POST** `/permissions`

**Request Body:**
```json
{
  "name": "string",
  "code": "string",
  "description": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "PERMISSION_GRANTED",
  "data": {
    "id": 1,
    "name": "string",
    "code": "string",
    "description": "string"
  }
}
```

### 3.2 Update Permission
**PUT** `/permissions/{id}`

**Request Body:**
```json
{
  "name": "string",
  "code": "string",
  "description": "string"
}
```

### 3.3 Delete Permission
**DELETE** `/permissions/{id}`

### 3.4 Get Permission by ID
**GET** `/permissions/{id}`

### 3.5 Get All Permissions
**GET** `/permissions`

---

## 4. Role APIs
*Requires: `ADMIN_FULL_ACCESS` authority*

### 4.1 Create Role
**POST** `/roles`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "ROLE_ASSIGNED",
  "data": {
    "id": 1,
    "name": "string",
    "description": "string"
  }
}
```

### 4.2 Update Role
**PUT** `/roles/{id}`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

### 4.3 Delete Role
**DELETE** `/roles/{id}`

### 4.4 Get Role by ID
**GET** `/roles/{id}`

### 4.5 Get All Roles
**GET** `/roles`

---

## 5. Role-Permission APIs
*Requires: `user:manage` authority*

### 5.1 Assign Permissions to Role
**POST** `/role-permissions/assign-permissions-to-role`

**Request Body:**
```json
{
  "roleId": 1,
  "permissionIds": [1, 2, 3]
}
```

### 5.2 Remove Permissions from Role
**DELETE** `/role-permissions/remove-permissions-from-role`

**Request Body:**
```json
{
  "roleId": 1,
  "permissionIds": [1, 2, 3]
}
```

### 5.3 Assign Roles to Permission
**POST** `/role-permissions/assign-roles-to-permission`

**Request Body:**
```json
{
  "permissionId": 1,
  "roleIds": [1, 2, 3]
}
```

### 5.4 Remove Roles from Permission
**DELETE** `/role-permissions/remove-roles-from-permission`

**Request Body:**
```json
{
  "permissionId": 1,
  "roleIds": [1, 2, 3]
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "VALIDATION_ERROR",
  "data": null,
  "errors": ["Field validation messages"]
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "UNAUTHORIZED",
  "data": null
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "ACCESS_DENIED",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "INTERNAL_SERVER_ERROR",
  "data": null
}
```

## Notes for Frontend Development

1. **Token Management**: Store JWT tokens securely, implement auto-refresh logic
2. **File Upload**: Use FormData for avatar upload with proper validation
3. **Error Handling**: Check `success` field and handle different error codes
4. **Permissions**: Check user permissions before showing UI elements
5. **Validation**: Implement client-side validation matching server requirements

# API Documentation - Quizizz Backend

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Tất cả API (trừ auth endpoints) yêu cầu JWT token trong header:
```
Authorization: Bearer <your_jwt_token>
```

## Common Response Format
Lưu ý: Các API trả về theo cấu trúc chuẩn sau (không có trường success/timestamp):
```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": <response_data>
}
```

---

## 1. Authentication APIs

### 1.1 Register
**POST** `/auth/register`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "fullName": "string"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "USER_CREATED",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "fullName": "string"
  }
}
```

### 1.2 Login
**POST** `/auth/login`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "AUTH_LOGIN_SUCCESS",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 1.3 Logout
**POST** `/auth/logout`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "AUTH_LOGOUT_SUCCESS",
  "data": "Logout successful"
}
```

### 1.4 Refresh Token
**POST** `/auth/refresh`

**Request Body:**
```json
"refresh_token_string"
```

**Response:**
```json
{
  "status": 200,
  "message": "AUTH_TOKEN_REFRESHED",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 1.5 Reset Password
**POST** `/auth/reset-password`

**Request Body:**
```json
{
  "email": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "AUTH_PASSWORD_RESET_SUCCESS",
  "data": {
    "message": "Password reset successfully"
  }
}
```

---

## 2. Profile APIs

### 2.1 Get Profile
**GET** `/profile`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatarUrl": "string"
  }
}
```

### 2.2 Update Profile
**PUT** `/profile`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "fullName": "string",
  "email": "string"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatarUrl": "string"
  }
}
```

### 2.3 Update Avatar
**POST** `/profile/avatar`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Request Body (Form Data):**
```
file: <image_file> (max 5MB, image types only)
```

**Response:**
```json
{
  "status": 200,
  "message": "AVATAR_UPDATED",
  "data": {
    "avatarUrl": "string"
  }
}
```

### 2.4 Get Avatar URL
**GET** `/profile/avatar`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": 200,
  "message": "AVATAR_URL_RETRIEVED",
  "data": "avatar_url_string"
}
```

---

## 3. Permission APIs
*Requires: `permission:manage` authority*

### 3.1 Create Permission
**POST** `/permissions`

**Request Body:**
```json
{
  "name": "string",
  "code": "string",
  "description": "string"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "PERMISSION_GRANTED",
  "data": {
    "id": 1,
    "name": "string",
    "code": "string",
    "description": "string"
  }
}
```

### 3.2 Update Permission
**PUT** `/permissions/{id}`

**Request Body:**
```json
{
  "name": "string",
  "code": "string",
  "description": "string"
}
```

### 3.3 Delete Permission
**DELETE** `/permissions/{id}`

### 3.4 Get Permission by ID
**GET** `/permissions/{id}`

### 3.5 Get All Permissions
**GET** `/permissions`

---

## 4. Role APIs
*Requires: `ADMIN_FULL_ACCESS` authority*

### 4.1 Create Role
**POST** `/roles`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "ROLE_ASSIGNED",
  "data": {
    "id": 1,
    "name": "string",
    "description": "string"
  }
}
```

### 4.2 Update Role
**PUT** `/roles/{id}`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

### 4.3 Delete Role
**DELETE** `/roles/{id}`

### 4.4 Get Role by ID
**GET** `/roles/{id}`

### 4.5 Get All Roles
**GET** `/roles`

---

## 5. Role-Permission APIs
*Requires: `user:manage` authority*

### 5.1 Assign Permissions to Role
**POST** `/role-permissions/assign-permissions-to-role`

**Request Body:**
```json
{
  "roleId": 1,
  "permissionIds": [1, 2, 3]
}
```

### 5.2 Remove Permissions from Role
**DELETE** `/role-permissions/remove-permissions-from-role`

**Request Body:**
```json
{
  "roleId": 1,
  "permissionIds": [1, 2, 3]
}
```

### 5.3 Assign Roles to Permission
**POST** `/role-permissions/assign-roles-to-permission`

**Request Body:**
```json
{
  "permissionId": 1,
  "roleIds": [1, 2, 3]
}
```

### 5.4 Remove Roles from Permission
**DELETE** `/role-permissions/remove-roles-from-permission`

**Request Body:**
```json
{
  "permissionId": 1,
  "roleIds": [1, 2, 3]
}
```

---

## 6. Room APIs
Base path: `/rooms`

### 6.1 Tạo phòng mới
- Method: POST
- URL: `/rooms`
- Headers: `Authorization: Bearer <token>`
- Body (CreateRoomRequest):
```json
{
  "roomName": "string",
  "roomMode": "ONE_VS_ONE | BATTLE_ROYAL",
  "topicId": 0,
  "isPrivate": false,
  "maxPlayers": 10,
  "questionCount": 10,
  "questionType": "string",
  "countdownTime": 30
}
```
- Response (RoomResponse):
```json
{
  "status": 200,
  "message": "ROOM_CREATED",
  "data": {
    "id": 1,
    "roomCode": "ABC123",
    "roomName": "string",
    "roomMode": "ONE_VS_ONE | BATTLE_ROYAL",
    "topicId": 1,
    "topicName": "string",
    "isPrivate": false,
    "ownerId": 1,
    "ownerUsername": "string",
    "status": "WAITING | PLAYING | FULL | FINISHED | ARCHIVED",
    "maxPlayers": 10,
    "currentPlayers": 1,
    "questionCount": 10,
    "questionType": "string",
    "countdownTime": 30,
    "createdAt": "2025-01-01T12:00:00"
  }
}
```

### 6.2 Lấy thông tin phòng theo ID
- Method: GET
- URL: `/rooms/{roomId}`
- Path params: `roomId` (Long)
- Response: `ApiResponse<RoomResponse>`

### 6.3 Lấy thông tin phòng theo room code
- Method: GET
- URL: `/rooms/code/{roomCode}`
- Path params: `roomCode` (String)
- Response: `ApiResponse<RoomResponse>`

### 6.4 Tìm kiếm nhanh theo room code hoặc tên phòng
- Method: GET
- URL: `/rooms/quick-search`
- Query params: `q` (String, bắt buộc)
- Response: `ApiResponse<List<RoomResponse>>`

### 6.5 Cập nhật thông tin phòng
- Method: PUT
- URL: `/rooms/{roomId}`
- Headers: `Authorization`
- Path params: `roomId` (Long)
- Body (UpdateRoomRequest):
```json
{
  "roomName": "string",
  "roomMode": "ONE_VS_ONE | BATTLE_ROYAL",
  "isPrivate": false,
  "maxPlayers": 10,
  "questionCount": 10,
  "questionType": "string",
  "countdownTime": 30
}
```
- Response: `ApiResponse<RoomResponse>` (message: "ROOM_UPDATED")

### 6.6 Xóa phòng
- Method: DELETE
- URL: `/rooms/{roomId}`
- Headers: `Authorization`
- Path params: `roomId` (Long)
- Response: `ApiResponse<Void>` (message: "ROOM_DELETED")

### 6.7 Lấy danh sách phòng của user (không phân trang)
- Method: GET
- URL: `/rooms/my-rooms`
- Headers: `Authorization`
- Response: `ApiResponse<List<RoomResponse>>`

### 6.8 Lấy danh sách phòng public (có phân trang)
- Method: GET
- URL: `/rooms/public`
- Query params:
  - `status` (RoomStatus, mặc định `WAITING`)
  - `page` (int, mặc định 0)
  - `size` (int, mặc định 4)
  - `search` (String, tùy chọn)
- Response: `ApiResponse<PagedRoomResponse>`

### 6.9 Lấy tất cả phòng (public + private) có phân trang
- Method: GET
- URL: `/rooms/all`
- Query params giống 6.8
- Response: `ApiResponse<PagedRoomResponse>`

### 6.10 Tìm kiếm phòng theo tên hoặc room code
- Method: GET
- URL: `/rooms/search`
- Query params:
  - `query` (String, bắt buộc)
  - `status` (RoomStatus, mặc định `WAITING`)
- Response: `ApiResponse<List<RoomResponse>>`

### 6.11 Lấy danh sách phòng của user (có phân trang)
- Method: GET
- URL: `/rooms/my-rooms/paged`
- Headers: `Authorization`
- Query params:
  - `page` (int, mặc định 0)
  - `size` (int, mặc định 4)
  - `search` (String, tùy chọn)
- Response: `ApiResponse<PagedRoomResponse>`

### 6.12 Tham gia phòng bằng room code
- Method: POST
- URL: `/rooms/join`
- Headers: `Authorization`
- Body (JoinRoomRequest):
```json
{
  "roomCode": "ABC123"
}
```
- Response: `ApiResponse<RoomResponse>` (message: "ROOM_JOINED")

### 6.13 Tham gia phòng public trực tiếp bằng room ID
- Method: POST
- URL: `/rooms/{roomId}/join-direct`
- Headers: `Authorization`
- Path params: `roomId` (Long)
- Response: `ApiResponse<RoomResponse>` (message: "ROOM_JOINED")

### 6.14 Rời khỏi phòng
- Method: DELETE
- URL: `/rooms/{roomId}/leave`
- Headers: `Authorization`
- Path params: `roomId` (Long)
- Response: `ApiResponse<Void>` (message: "ROOM_LEFT")

### 6.15 Lấy danh sách người chơi trong phòng
- Method: GET
- URL: `/rooms/{roomId}/players`
- Path params: `roomId` (Long)
- Response: `ApiResponse<List<RoomPlayerResponse>>`

### 6.16 Kick người chơi khỏi phòng
- Method: DELETE
- URL: `/rooms/{roomId}/kick`
- Headers: `Authorization` (Host)
- Path params: `roomId` (Long)
- Body (KickPlayerRequest):
```json
{
  "playerId": 0,
  "reason": "string"
}
```
- Response: `ApiResponse<Void>` (message: "PLAYER_LEFT_GAME")

### 6.17 Mời người chơi vào phòng
- Method: POST
- URL: `/rooms/invite`
- Headers: `Authorization`
- Body (InvitePlayerRequest):
```json
{
  "roomId": 1,
  "username": "inviteeUsername",
  "message": "Join my room"
}
```
- Response: `ApiResponse<InvitationResponse>`

### 6.18 Phản hồi lời mời
- Method: POST
- URL: `/rooms/invitations/{invitationId}/respond`
- Headers: `Authorization`
- Path params: `invitationId` (Long)
- Query params: `accept` (boolean)
- Response:
  - Nếu `accept=true`: `ApiResponse<RoomResponse>` (message: "ROOM_JOINED")
  - Nếu `accept=false`: `ApiResponse<Void>` (message: "SUCCESS")

### 6.19 Lấy danh sách lời mời của user
- Method: GET
- URL: `/rooms/invitations`
- Headers: `Authorization`
- Response: `ApiResponse<List<InvitationResponse>>`

### 6.20 Chuyển quyền host
- Method: POST
- URL: `/rooms/{roomId}/transfer-host`
- Headers: `Authorization` (Host)
- Path params: `roomId` (Long)
- Query params: `newHostId` (Long)
- Response: `ApiResponse<RoomResponse>` (message: "ROOM_HOST_TRANSFERRED")

### 6.21 Bắt đầu game
- Method: POST
- URL: `/rooms/{roomId}/start`
- Headers: `Authorization` (Host)
- Path params: `roomId` (Long)
- Response: `ApiResponse<Void>` (message: "GAME_STARTED")

### 6.22 Schema chi tiết
- RoomResponse:
```json
{
  "id": 0,
  "roomCode": "string",
  "roomName": "string",
  "roomMode": "ONE_VS_ONE | BATTLE_ROYAL",
  "topicId": 0,
  "topicName": "string",
  "isPrivate": false,
  "ownerId": 0,
  "ownerUsername": "string",
  "status": "WAITING | PLAYING | FULL | FINISHED | ARCHIVED",
  "maxPlayers": 10,
  "currentPlayers": 1,
  "questionCount": 10,
  "questionType": "string",
  "countdownTime": 30,
  "createdAt": "2025-01-01T12:00:00"
}
```
- PagedRoomResponse:
```json
{
  "rooms": [ /* RoomResponse[] */ ],
  "currentPage": 0,
  "totalPages": 0,
  "totalElements": 0,
  "pageSize": 0,
  "hasNext": true,
  "hasPrevious": false
}
```
- RoomPlayerResponse:
```json
{
  "id": 0,
  "userId": 0,
  "username": "string",
  "isHost": false,
  "joinedAt": "2025-01-01T12:00:00"
}
```
- InvitationResponse:
```json
{
  "id": 0,
  "roomId": 0,
  "roomName": "string",
  "inviterId": 0,
  "inviterUsername": "string",
  "inviteeId": 0,
  "inviteeUsername": "string",
  "status": "PENDING | ACCEPTED | DECLINED",
  "createdAt": "2025-01-01T12:00:00",
  "expiresAt": "2025-01-01T13:00:00"
}
```

### 6.23 Enum values
- RoomMode: `ONE_VS_ONE`, `BATTLE_ROYAL`
- RoomStatus: `WAITING`, `PLAYING`, `FULL`, `FINISHED`, `ARCHIVED`

---

## Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "VALIDATION_ERROR",
  "data": null
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "UNAUTHORIZED",
  "data": null
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "ACCESS_DENIED",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "INTERNAL_SERVER_ERROR",
  "data": null
}
```

## Notes for Frontend Development

1. Token Management: Lưu JWT an toàn, làm mới token khi cần.
2. File Upload: Dùng FormData cho upload avatar, kiểm tra định dạng/kích thước.
3. Error Handling: Dựa vào `status` và `message` trong response.
4. Permissions: Kiểm tra quyền trước khi hiển thị tính năng.
5. Validation: Áp dụng kiểm tra dữ liệu đầu vào giống server.
