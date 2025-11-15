# Postman Collection Guide

## Importing the Collection

1. Open Postman
2. Click **Import** button (top left)
3. Select the file `Video_Streaming_API.postman_collection.json`
4. The collection will be imported with all requests organized in folders

## Collection Variables

The collection includes the following variables that are automatically managed:

- `baseUrl` - Set to `http://localhost:8080` (change if your server runs on a different port)
- `authToken` - Automatically saved after login/register
- `userId` - Automatically saved after login/register
- `username` - Automatically saved after login/register

### To Update Base URL:
1. Click on the collection name
2. Go to **Variables** tab
3. Update the `baseUrl` value if needed

## Using the Collection

### Step 1: Register or Login
1. Open **Authentication** folder
2. Run **Register User** or **Login** request
3. The JWT token will be automatically saved to `authToken` variable
4. This token will be used automatically for authenticated requests

### Step 2: Test Video Endpoints
1. **List All Videos** - Get paginated list of videos
2. **Search Videos** - Search videos by title/description
3. **Get Video by ID** - Replace `:id` with actual video ID
4. **Upload Video** - Select a video file and fill in title/description
5. **Stream Video** - Replace `:filename` with actual filename from video response

## Request Details

### Authentication Requests

#### Register User
- **Method:** POST
- **URL:** `/api/auth/register`
- **Body:** JSON with username, email, password
- **Auto-saves:** Token, userId, username

#### Login
- **Method:** POST
- **URL:** `/api/auth/login`
- **Body:** JSON with username, password
- **Auto-saves:** Token, userId, username

### Video Requests

#### List All Videos
- **Method:** GET
- **URL:** `/api/videos`
- **Query Params:** 
  - `page` (default: 0)
  - `size` (default: 10)
  - `search` (optional)

#### Search Videos
- **Method:** GET
- **URL:** `/api/videos?search=query`
- Pre-configured with search parameter

#### Get Video by ID
- **Method:** GET
- **URL:** `/api/videos/:id`
- Replace `:id` with actual video ID from list response

#### Upload Video
- **Method:** POST
- **URL:** `/api/videos/upload`
- **Auth:** Bearer token (auto-included)
- **Body:** form-data
  - `file` - Select video file
  - `title` - Video title
  - `description` - Video description (optional)

#### Stream Video
- **Method:** GET
- **URL:** `/api/videos/stream/:filename`
- Replace `:filename` with filename from video response
- Optional: Add `Range` header for partial content

## Testing Workflow

1. **Register a new user** or **Login** with existing credentials
2. **List videos** to see available videos
3. **Upload a video** (requires authentication - token is auto-included)
4. **Get video details** by ID from the list
5. **Stream video** using the filename from video details

## Tips

- The JWT token is automatically saved after login/register
- All authenticated requests automatically include the Bearer token
- You can update the `baseUrl` variable to point to different environments
- Use the **Search Videos** request to test search functionality
- The **Stream Video with Range** request demonstrates range request support

## Troubleshooting

### Token Not Working
- Make sure you've run Login or Register first
- Check that the token is saved in collection variables
- Verify the token hasn't expired (default: 24 hours)

### Upload Fails
- Ensure you're authenticated (token is set)
- Check file size (max 500MB)
- Verify file is a valid video format

### Video Not Found
- Make sure you're using the correct video ID or filename
- Check that the video was successfully uploaded
- Verify the baseUrl is correct

