# Video Streaming API Routes

## Base URL
`http://localhost:8080`

## Authentication Endpoints

### 1. Register User
- **Endpoint:** `POST /api/auth/register`
- **Authentication:** Not required
- **Request Body:**
```json
{
  "username": "string (3-20 chars)",
  "email": "valid email",
  "password": "string (min 6 chars)"
}
```
- **Response:** 
```json
{
  "token": "JWT token",
  "type": "Bearer",
  "username": "string",
  "userId": "string"
}
```

### 2. Login
- **Endpoint:** `POST /api/auth/login`
- **Authentication:** Not required
- **Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response:**
```json
{
  "token": "JWT token",
  "type": "Bearer",
  "username": "string",
  "userId": "string"
}
```

## Video Endpoints

### 3. List All Videos (with pagination and search)
- **Endpoint:** `GET /api/videos`
- **Authentication:** Not required
- **Query Parameters:**
  - `page` (default: 0) - Page number
  - `size` (default: 10) - Items per page
  - `search` (optional) - Search query for title/description
- **Response:**
```json
{
  "videos": [
    {
      "id": "string",
      "title": "string",
      "description": "string",
      "videoUrl": "/api/videos/stream/{filename}",
      "uploaderId": "string",
      "uploaderUsername": "string",
      "uploadDate": "timestamp",
      "fileSize": "number",
      "contentType": "string"
    }
  ],
  "currentPage": 0,
  "totalItems": 100,
  "totalPages": 10
}
```

### 4. Get Video by ID
- **Endpoint:** `GET /api/videos/{id}`
- **Authentication:** Not required
- **Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "videoUrl": "/api/videos/stream/{filename}",
  "uploaderId": "string",
  "uploaderUsername": "string",
  "uploadDate": "timestamp",
  "fileSize": "number",
  "contentType": "string"
}
```

### 5. Upload Video
- **Endpoint:** `POST /api/videos/upload`
- **Authentication:** Required (Bearer token in Authorization header)
- **Content-Type:** `multipart/form-data`
- **Form Data:**
  - `file` - Video file (max 500MB)
  - `title` - Video title (required)
  - `description` - Video description (optional)
- **Headers:**
  - `Authorization: Bearer {token}`
- **Response:**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "videoUrl": "/api/videos/stream/{filename}",
  "uploaderId": "string",
  "uploaderUsername": "string",
  "uploadDate": "timestamp",
  "fileSize": "number",
  "contentType": "string"
}
```

### 6. Stream Video
- **Endpoint:** `GET /api/videos/stream/{filename}`
- **Authentication:** Not required
- **Headers (optional):**
  - `Range: bytes=start-end` - For partial content/seek support
- **Response:** Video file stream with proper content-type and range support

## Home Endpoint

### 7. API Information
- **Endpoint:** `GET /`
- **Authentication:** Not required
- **Response:** API information and available endpoints

## Authentication

For protected endpoints, include the JWT token in the Authorization header:
```
Authorization: Bearer {your-jwt-token}
```

## Error Responses

All endpoints return error responses in the following format:
```json
{
  "error": "Error message description"
}
```

Common HTTP status codes:
- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required or invalid credentials
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

