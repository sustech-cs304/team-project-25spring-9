

[toc]



# Architecture & UI Design

## Architectural Design



![image-20250412174418928](design-09/image-20250412174418928.png)

This platform adopts a **microservices-based and modular design** that cleanly separates core responsibilities such as user management, photo storage and filtering, AI-powered tagging, photo editing, social collaboration. This architecture ensures **maintainability**, **scalability**, and **extensibility** for future enhancements.

### Frontend Layer

The frontend, built with **Vue.js**, delivers a responsive and user-friendly experience across devices.
 It supports:

- Dynamic photo browsing and editing
- Album management
- Social collaboration features (commenting, reacting, sharing)
- Smooth integration with backend services via **RESTful APIs**

Styling is enhanced through **TailwindCSS** and **Bootstrap**, enabling rapid and consistent UI development.

### API Gateway

The API Gateway provides a **centralized entry point**, responsible for:

- Routing client requests to backend services
- Handling authentication and authorization
- Enabling logging, monitoring, and throttling

This promotes secure, maintainable, and observable service communication.

###  Backend Microservices

The backend includes services developed in **Python (FastAPI)** and **Java (Spring Boot)**, encapsulated by function:

####  User Service

- Handles registration, login, password reset, and **OAuth2 authentication**
- Manages user roles, permissions, and privacy settings

####  Album Service

- Manages photo collections, album access control, and tagging metadata
- Allows users to create, modify, and share albums
- Supports fast filtering by tags, people, location, or date

#### Auto-tagging Service

- Extract the meta data through EXIF
- Uses **OpenCV** for face/object recognition and preprocessing
- Applies **GPT/Image Description Model** for captioning and auto-tagging

#### Photo Editing Service

- Offers editing tools such as cropping, filtering, rotating, and enhancing

#### Memory Video Generation Service

- Uses **MoviePy** and **FFmpeg** to convert photos into stylized memory videos
- Supports transitions, music, and personalized storytelling

#### Social Collaboration Service

- **Shared Albums**: Users can invite others to view their albums through sending a sharing link and contribute to albums with configurable access rights
- **Comments & Reactions**: Enables commenting on photos, replying to threads, and reacting with emojis
- **Activity Feed**: Provides real-time updates on shared content (e.g., new uploads, reactions)

#### AI-powered Customed Album

- Leveraging on-prime Stable Diffusion model to generate customed albums

### Data Storage Layer

- **MySQL**: Stores structured data, such as user info, album metadata, comments, and tags
- **MINIO**: Object storage system for unstructured data like photos and videos

### Design Assumptions & Extensibility

- **We assume that all the EXIF information can be extracted from the uploaded images.**

  

- **Inter-service Communication Mechanism Not Specified**

  

  - **Assumption**: All microservices communicate via HTTP REST or **gRPC**, with stable and low-latency connections.

  - **Risk**: Reliability under high concurrency, retry strategies, and circuit breaker mechanisms are not addressed.

- **Frontend and API Gateway Integration Assumes No CORS Issues**
  - **Assumption**: The frontend is deployed under the same domain as the API Gateway, or appropriate CORS policies are configured. Otherwise, frontend-backend communication would be blocked by browsers.
- **AI Model Deployment and Resource Availability**
  - **Assumption**: The servers hosting OpenCV, Stable Diffusion, and GPT/Image Description models have sufficient GPU resources or are containerized.
  - **Risk**: Issues like inference latency, cold start time, and batch processing queues are not discussed.
- **Multi-language Microservice Interoperability**
  - **Assumption**: Services built with Python (FastAPI) and Java (Spring Boot) can interoperate seamlessly without version conflicts or encoding/decoding issues.
- **MINIO Access Control and Capacity Planning Not Specified**
  - **Assumption**: MINIO is properly configured with access control policies, object lifecycle management, and has sufficient redundancy to support high availability.

- **Push notifications, chat features, and social integrations are planned for future releases**



## UI Design





