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