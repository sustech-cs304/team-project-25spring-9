

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

### **Two-Panel Layout (Sidebar + Main View)**

- The screen is split into a **left-side vertical menu (navigation)** and a **right-side content area (dynamic dashboard)**.
- **Why**:
  - This pattern allows users to **consistently access navigation options** without leaving the current view.
  - Promotes **quick context switching** (e.g., from Dashboard to Photos) with minimal friction.
- **Hidden assumption**:
  - The app doesn’t rely on deep nested navigation. It assumes users only need **top-level categories** (Dashboard, Photos, Timeline, etc.).

### **Card-Based Metric Display**

- Each metric like **Total Photos** or **Image** is placed in an individual card.
- **Why**:
  - Cards offer a **flexible, reusable component** to display various types of data (text, icons, images, charts).
  - They are **easy to expand** or re-order (e.g., add "Most Active Day" in the future).

### **Iconography & Visual Cues**

- Icons complement the text to reinforce meaning visually (e.g., a mountain image icon for “photos”).
- **Why**:
  - Visual shorthand reduces cognitive load, especially for repeat users.
  - Enhances accessibility and UX through **dual-coding (text + visual)**.
- **Hidden assumption**:
  - Users are familiar with basic visual conventions (clock = time, image = photos).

### **Responsive Component Design**

- Though not visible in the wireframe, the layout likely supports **responsive behavior**.
- **Why**:
  - Users might access the dashboard on tablets or desktops.
  - Components like the sidebar, cards, and search fields are commonly **built using a grid or flex-based layout system** for adaptability.
- **Hidden assumption**:
  - The team is using a component library or custom CSS system (e.g., Tailwind CSS or Bootstrap) to ensure consistency.

### 🧩 **What the Diagram Doesn't Show (Hidden Assumptions)**

- **State management**:
  - Not shown, but the app likely uses a central state management solution (Pinia).
- **API communication**:
  - Assumes there's an API (REST) delivering the data shown in the dashboard.
- **Security layers**:
  - Login/authentication flows aren’t visible but must exist (implied by user profile access).
- **Error and loading states**:
  - UI would need to handle empty states, errors, and spinners for real-world usage.
