# Video Streaming API - Postman Test Guide

## Base URL
```
http://localhost:8080
```

---

## 1. Register User

**Endpoint:** `POST /api/auth/register`  
**Authentication:** Not required  
**Content-Type:** `application/json`

### Request Body:
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

### Example Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "johndoe",
  "userId": "507f1f77bcf86cd799439011"
}
```

### Postman Setup:
1. Method: **POST**
2. URL: `http://localhost:8080/api/auth/register`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw JSON):
   ```json
   {
     "username": "johndoe",
     "email": "john.doe@example.com",
     "password": "password123"
   }
   ```

### Validation Rules:
- Username: 3-20 characters
- Email: Valid email format
- Password: Minimum 6 characters

---

## 2. Login

**Endpoint:** `POST /api/auth/login`  
**Authentication:** Not required  
**Content-Type:** `application/json`

### Request Body:
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

### Example Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "johndoe",
  "userId": "507f1f77bcf86cd799439011"
}
```

### Postman Setup:
1. Method: **POST**
2. URL: `http://localhost:8080/api/auth/login`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw JSON):
   ```json
   {
     "username": "johndoe",
     "password": "password123"
   }
   ```

### Error Response (401 Unauthorized):
```json
{
  "error": "Invalid username or password"
}
```

---

## 3. List All Videos (with Pagination)

**Endpoint:** `GET /api/videos`  
**Authentication:** Not required

### Query Parameters:
- `page` (optional, default: 0) - Page number (0-indexed)
- `size` (optional, default: 10) - Number of items per page
- `search` (optional) - Search query for title/description

### Example Requests:

#### Get first page (default):
```
GET http://localhost:8080/api/videos
```

#### Get second page with 20 items:
```
GET http://localhost:8080/api/videos?page=1&size=20
```

#### Search videos:
```
GET http://localhost:8080/api/videos?search=tutorial
```

### Postman Setup:
1. Method: **GET**
2. URL: `http://localhost:8080/api/videos`
3. Query Params (optional):
   - `page`: 0
   - `size`: 10
   - `search`: tutorial

### Example Response (200 OK):
```json
{
  "videos": [
    {
      "id": "507f1f77bcf86cd799439011",
      "title": "Introduction to Spring Boot",
      "description": "Learn the basics of Spring Boot framework",
      "videoUrl": "/api/videos/stream/abc123-def456-ghi789.mp4",
      "uploaderId": "507f1f77bcf86cd799439012",
      "uploaderUsername": "johndoe",
      "uploadDate": "2024-01-15T10:30:00.000Z",
      "fileSize": 52428800,
      "contentType": "video/mp4"
    }
  ],
  "currentPage": 0,
  "totalItems": 25,
  "totalPages": 3
}
```

---

## 4. Get Video by ID

**Endpoint:** `GET /api/videos/{id}`  
**Authentication:** Not required

### Example Request:
```
GET http://localhost:8080/api/videos/507f1f77bcf86cd799439011
```

### Postman Setup:
1. Method: **GET**
2. URL: `http://localhost:8080/api/videos/507f1f77bcf86cd799439011`
   - Replace `507f1f77bcf86cd799439011` with actual video ID

### Example Response (200 OK):
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "Introduction to Spring Boot",
  "description": "Learn the basics of Spring Boot framework",
  "videoUrl": "/api/videos/stream/abc123-def456-ghi789.mp4",
  "uploaderId": "507f1f77bcf86cd799439012",
  "uploaderUsername": "johndoe",
  "uploadDate": "2024-01-15T10:30:00.000Z",
  "fileSize": 52428800,
  "contentType": "video/mp4"
}
```

### Error Response (404 Not Found):
```json
{
  "error": "Video not found"
}
```

---

## 5. Upload Video

**Endpoint:** `POST /api/videos/upload`  
**Authentication:** Required (Bearer token)  
**Content-Type:** `multipart/form-data`

### Request Body (Form Data):
- `file` (required) - Video file (max 500MB)
- `title` (required) - Video title
- `description` (optional) - Video description

### Postman Setup:
1. Method: **POST**
2. URL: `http://localhost:8080/api/videos/upload`
3. Headers:
   - `Authorization: Bearer {your-jwt-token}`
   - Note: Do NOT set Content-Type header manually for multipart/form-data
4. Body (form-data):
   - `file`: [Select File] - Choose a video file (e.g., .mp4, .avi, .mov)
   - `title`: "My First Video"
   - `description`: "This is a test video upload"

### Example Form Data:
```
Key: file          | Type: File      | Value: [Select video file]
Key: title         | Type: Text      | Value: My First Video
Key: description   | Type: Text      | Value: This is a test video upload
```

### Example Response (201 Created):
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "My First Video",
  "description": "This is a test video upload",
  "videoUrl": "/api/videos/stream/abc123-def456-ghi789.mp4",
  "uploaderId": "507f1f77bcf86cd799439012",
  "uploaderUsername": "johndoe",
  "uploadDate": "2024-01-15T10:30:00.000Z",
  "fileSize": 52428800,
  "contentType": "video/mp4"
}
```

### Error Responses:

#### 401 Unauthorized (No token):
```json
{
  "error": "Unauthorized"
}
```

#### 400 Bad Request (Empty file):
```json
{
  "error": "File is empty"
}
```

#### 400 Bad Request (Missing title):
```json
{
  "error": "Title is required"
}
```

---

## 6. Stream Video

**Endpoint:** `GET /api/videos/stream/{filename}`  
**Authentication:** Not required  
**Supports:** HTTP Range Requests (for video seeking)

### Example Request:
```
GET http://localhost:8080/api/videos/stream/abc123-def456-ghi789.mp4
```

### Postman Setup:

#### Basic Streaming:
1. Method: **GET**
2. URL: `http://localhost:8080/api/videos/stream/abc123-def456-ghi789.mp4`
   - Replace `abc123-def456-ghi789.mp4` with actual filename from video response

#### With Range Request (for seeking):
1. Method: **GET**
2. URL: `http://localhost:8080/api/videos/stream/abc123-def456-ghi789.mp4`
3. Headers:
   - `Range: bytes=0-1023` (first 1024 bytes)
   - `Range: bytes=1024-2047` (next 1024 bytes)
   - `Range: bytes=0-` (from start to end)

### Example Range Header Values:
- `bytes=0-` - From start to end (full video)
- `bytes=0-1023` - First 1024 bytes
- `bytes=1024-2047` - Bytes 1024 to 2047
- `bytes=52428800-` - From byte 52428800 to end

### Response:
- **200 OK** - Full video stream
- **206 Partial Content** - Partial video stream (when Range header is used)
- **404 Not Found** - Video file not found

### Response Headers:
```
Content-Type: video/mp4
Content-Length: 52428800
Accept-Ranges: bytes
Content-Range: bytes 0-1023/52428800 (for partial content)
```

---

## 7. API Information (Home)

**Endpoint:** `GET /`  
**Authentication:** Not required

### Postman Setup:
1. Method: **GET**
2. URL: `http://localhost:8080/`

### Example Response (200 OK):
```json
{
  "message": "Welcome to Video Streaming App!",
  "endpoints": {
    "register": "POST /api/auth/register",
    "login": "POST /api/auth/login",
    "listVideos": "GET /api/videos?page=0&size=10&search=query",
    "getVideo": "GET /api/videos/{id}",
    "uploadVideo": "POST /api/videos/upload (requires authentication)",
    "streamVideo": "GET /api/videos/stream/{filename}"
  }
}
```

---

## Authentication Setup in Postman

### Method 1: Manual Header (Recommended for Testing)
1. After login/register, copy the `token` from the response
2. For authenticated requests, add header:
   - Key: `Authorization`
   - Value: `Bearer {paste-token-here}`

### Method 2: Environment Variables
1. Create a Postman Environment
2. Add variable: `authToken`
3. After login/register, set the token:
   ```javascript
   // In Tests tab of login/register request:
   pm.environment.set("authToken", pm.response.json().token);
   ```
4. Use in Authorization header:
   - Key: `Authorization`
   - Value: `Bearer {{authToken}}`

---

## Complete Testing Workflow

### Step 1: Register a New User
```
POST /api/auth/register
Body: {
  "username": "testuser",
  "email": "test@example.com",
  "password": "test123"
}
```
**Save the token from response!**

### Step 2: Login (Alternative to Register)
```
POST /api/auth/login
Body: {
  "username": "testuser",
  "password": "test123"
}
```
**Save the token from response!**

### Step 3: List All Videos
```
GET /api/videos?page=0&size=10
```

### Step 4: Search Videos
```
GET /api/videos?search=tutorial&page=0&size=10
```

### Step 5: Get Video Details
```
GET /api/videos/{video-id}
```
Use an ID from Step 3 response.

### Step 6: Upload a Video
```
POST /api/videos/upload
Headers: Authorization: Bearer {your-token}
Body (form-data):
  - file: [select video file]
  - title: "Test Video"
  - description: "Testing video upload"
```

### Step 7: Stream Video
```
GET /api/videos/stream/{filename}
```
Use filename from Step 5 or Step 6 response.

### Step 8: Stream Video with Range (Test Seeking)
```
GET /api/videos/stream/{filename}
Headers: Range: bytes=0-1023
```

---

## Common Error Responses

### 400 Bad Request
```json
{
  "error": "Error message description"
}
```

### 401 Unauthorized
```json
{
  "error": "Invalid username or password"
}
```
or
```json
{
  "error": "Unauthorized"
}
```

### 404 Not Found
```json
{
  "error": "Video not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Failed to fetch videos: Error details"
}
```

---

## Tips for Testing

1. **Token Management**: Save your JWT token after login/register. Tokens expire after 24 hours (86400000 ms).

2. **File Upload**: 
   - Maximum file size: 500MB
   - Supported formats: Any video format (mp4, avi, mov, etc.)
   - Use actual video files for testing

3. **Pagination**: 
   - Page numbers start at 0
   - Default page size is 10
   - Adjust `size` parameter for more/fewer results

4. **Search**: 
   - Search is case-insensitive
   - Searches in both title and description
   - Combine with pagination: `?search=query&page=0&size=20`

5. **Range Requests**: 
   - Essential for video players to support seeking
   - Test with different byte ranges
   - Browser video players automatically use range requests

6. **CORS**: The API allows all origins (`*`), so you can test from any frontend.

---

## Postman Collection Variables

If using the Postman collection, these variables are available:

- `{{baseUrl}}` - Base URL (default: `http://localhost:8080`)
- `{{authToken}}` - JWT token (auto-saved after login/register)
- `{{userId}}` - User ID (auto-saved after login/register)
- `{{username}}` - Username (auto-saved after login/register)

### Example Usage:
```
GET {{baseUrl}}/api/videos
Authorization: Bearer {{authToken}}
```

