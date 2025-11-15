# Video Streaming Microproject (React + Spring Boot + MongoDB)

## Functional Requirements

### Overview

Develop a simplified video streaming platform (YouTube-like) supporting:

- Video upload
- Video playback/streaming
- User registration \& login
- Video listing and search


### Minimum Core Features

- **User registration \& login:** Users can register, login, and log out.
- **Video upload:** Authenticated users can upload videos with basic metadata (title, description).
- **Video playback:** Users can view and stream uploaded videos.
- **Video listing:** Users can see a paginated/searchable list of all videos.
- **Basic RESTful APIs:** Backend exposes REST endpoints for CRUD operations.

***

## Spring Boot Backend Instructions (For IDE Kiro)

1. **Initialize Spring Boot Project**
    - Use Spring Initializr or similar (supported in Kiro).
    - Add dependencies: `Spring Web`, `Spring Data MongoDB`, `Spring Security`.
2. **Configure MongoDB Connection**
    - In `application.properties`:

```
spring.data.mongodb.uri=mongodb://localhost:27017/videostream
```

3. **Create Model Classes**
    - Create Java model classes for `User` and `Video` as shown below.
4. **Repository Interfaces**
    - Use `@Repository` interfaces extending `MongoRepository<User, String>` and `MongoRepository<Video, String>`.
5. **Service Layer**
    - Implement service classes for business logic: video management, authentication.
6. **Controller Layer**
    - Create REST controllers (`@RestController`) for user and video endpoints.
7. **Video Upload**
    - Use multipart upload (Spring Boot's `MultipartFile`) for videos.
    - Store files in MongoDB using GridFS or on filesystem with metadata in MongoDB.
8. **Video Streaming**
    - Stream videos using endpoint with byte-range support for efficient playback.
9. **Authentication**
    - Add simple JWT authentication or session-based login (keep it minimal).

***

## MongoDB Model Design

### User Model

```java
@Document("users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
}
```


### Video Model

```java
@Document("videos")
public class Video {
    @Id
    private String id;
    private String title;
    private String description;
    private String videoUrl; // Or GridFS ObjectId if using GridFS
    private String uploaderId;
    private Date uploadDate;
}
```


***

## Essential Endpoints Specification

| Endpoint | Method | Description |
| :-- | :-- | :-- |
| `/api/auth/register` | POST | Register new user |
| `/api/auth/login` | POST | User login, receive token/session |
| `/api/videos` | GET | List all videos (searchable) |
| `/api/videos/upload` | POST | Upload new video (auth required) |
| `/api/videos/{id}` | GET | Stream video by ID |


***

## Sample Project Structure (Backend)

```
src/
  main/
    java/
      com/yourdomain/videostream/
        model/
          User.java
          Video.java
        repository/
          UserRepository.java
          VideoRepository.java
        service/
          UserService.java
          VideoService.java
        controller/
          AuthController.java
          VideoController.java
    resources/
      application.properties
```


**Keep the UI basic but modern with React – focus on backend efficiency!**[^2][^3][^4][^1]

***


[^1]: https://www.javaguides.net/2021/08/react-spring-boot-mongodb-crud-example.html

[^2]: https://www.youtube.com/watch?v=hSRZxHPuaWg

[^3]: https://www.youtube.com/watch?v=ctJwoMZt-Nc

[^4]: https://seldomindia.com/building-a-video-streaming-application-with-java-and-spring-boot/

[^5]: https://dev.to/torver213/build-a-full-stack-video-streaming-app-with-reactjs-nodejs-nextjs-mongodb-bunny-cdn-and-2nii

[^6]: https://stackoverflow.com/questions/50005378/building-a-react-spring-boot-mongodb-application

[^7]: https://www.youtube.com/watch?v=5PdEmeopJVQ

[^8]: https://dev.to/abdisalan_js/how-to-stream-video-from-mongodb-using-nodejs-4ibi

[^9]: https://www.youtube.com/watch?v=6DKfxZdeKF0

[^10]: https://www.youtube.com/watch?v=_UNE39gZrV4

[^11]: https://www.youtube.com/watch?v=y6Z-SZt-Xvw

[^12]: https://www.youtube.com/watch?v=d6gu5wZtj2c

[^13]: https://www.guvi.in/blog/top-spring-project-ideas-with-source-code/

[^14]: https://kiro.dev/docs/guides/languages-and-frameworks/java-guide/

[^15]: https://www.geeksforgeeks.org/mongodb/top-mongodb-projects-ideas-for-beginners/

[^16]: https://www.youtube.com/watch?v=VR1zoNomG3w

[^17]: https://mosy.tech/blog/full-reactive-stack-spring-boot-webflux-mongodb/

[^18]: https://www.youtube.com/playlist?list=PL0zysOflRCemlosjVjP5MAam7EerAC_OG

[^19]: https://www.projectpro.io/article/mongodb-projects-ideas/640

[^20]: https://www.youtube.com/watch?v=gJrjgg1KVL4

