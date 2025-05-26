[toc]



# final-report-09



## Metrics

### Java Backend Metrics

| Lines of Code          | 3138 |
| ---------------------- | ---- |
| Number of source files | 82   |
| Cyclomatic complexity  | 1061 |
| Number of dependencies | 26   |

------

### Python Backend Metrics

#### 1. Lines of Code (LOC)

We used the [`cloc`](https://github.com/AlDanial/cloc) tool to analyze the code size:

```bash
cloc process_image.py
```

**Output:**

```
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Python                           1            128             80            472
-------------------------------------------------------------------------------
```

------

#### 2. Number of Source Files

All core functionalities are implemented in:

- `process_image.py` (main logic + FastAPI endpoints)

 **Total source files: 1**

------

#### 3. Cyclomatic Complexity

We evaluated the function complexity using the [`radon`](https://pypi.org/project/radon/) tool:

```bash
radon cc process_image.py -s -a
```

**Output :**

![image-20250525225600897](final-report-09/image-20250525225600897.png)

**Average cyclomatic complexity: A**

------

#### 4. Number of Dependencies

This project relies on a number of third-party libraries. You can generate a `requirements.txt` file using:

```bash
pip install pipreqs
pipreqs . --force
cat requirements.txt | wc -l
```

 Dependencies include but are not limited to:

- FastAPI
- face_recognition
- piexif
- transformers
- torch
- spacy
- uvicorn
- moviepy
- geopy
- pillow

**Number of dependencies: 18**

------



### Frontend Metrics

#### Cyclomatic Complexity

[Javascript Cyclomatic Complexity](./statistic/js-cyclomatic-complexity.json)

#### Cloc

```
      91 text files.
classified 91 files
      91 unique files.                              
       0 files ignored.

github.com/AlDanial/cloc v 1.96  T=0.04 s (2424.6 files/s, 225593.6 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Vuejs Component                 72            863            375           6125
JavaScript                      10             64             64            564
CSS                              9             76              7            329
-------------------------------------------------------------------------------
SUM:                            91           1003            446           7018
-------------------------------------------------------------------------------
```



## Documentation

### Documentation for developers

#### http://10.16.60.67:9090/swagger-ui.html#/

![img.png](final-report-09/img.png)

#### [🖼️ 图像智能处理后端 API - Swagger UI](http://10.16.60.67:8123/docs)

![image-20250525221802032](final-report-09/image-20250525221802032.png)

###  Documentation for end users

A modern Vue 3 and Tailwind CSS application for organizing, editing, and managing your photo collections with AI-powered styling capabilities.

#### ✨ Features

- **Photo Management**: Upload, organize, and view photos in a beautiful gallery interface
- **Album Organization**: Create and manage photo albums with drag-and-drop functionality
- **Timeline View**: Browse photos chronologically with an intuitive timeline interface
- **AI Photo Styling**: Transform your photos with AI-powered style transfer using Aliyun DashScope
- **Photo Editing**: Built-in photo editor with cropping, filters, and adjustment tools
- **Advanced Search & Filtering**: Powerful search capabilities with multiple filter criteria
- **Tag System**: Organize photos with custom tags for easy searching and filtering
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **User Profiles**: Personal user accounts with secure authentication

#### Architecture

![dependency-graph](final-report-09/dependency-graph.svg)

#### 📖 User Guide

##### Getting Started

1. **Create an Account**

   - Navigate to the registration page
     ![image-20250526114147717](final-report-09/image-20250526114147717.png)
   - Fill in your username, email, and password
     ![Register](final-report-09/register.png)
   - Complete the registration process

2. **Login**
   - Use your credentials to access your personal photo library

##### Managing Photos

###### Uploading Photos

- Navigate to the **Photos** section
- Click the upload button or drag and drop files into the photo area
- Supported formats: JPG, PNG, GIF, WebP
- Multiple files can be uploaded simultaneously
- Progress indicator shows upload status for each file

###### Deleting Photos

- Select photos using the checkbox selection mode
- Click the delete button in the toolbar
- Confirm deletion in the popup dialog
- **Note**: Deleted photos are permanently removed and cannot be recovered
- Alternatively, right-click on individual photos for quick delete option

###### Downloading Photos

- **Single Photo**: Click on a photo and select the download option
- **Multiple Photos**: Select photos using checkboxes and click the download button
- **Bulk Download**: Selected photos will be packaged into a ZIP file
- Downloads preserve original image quality and metadata
- Large selections may take time to process before download begins

![image-20250526114025942](final-report-09/image-20250526114025942.png)

##### Advanced Search & Filtering

The Smart Album includes powerful search and filtering capabilities to help you quickly find specific photos in your collection.

###### Quick Search

1. **Basic Text Search**
   - Use the search bar at the top of the Photos section
   - Search by photo name, file type, or any text field
   - Press Enter or click the search button to apply
   - Real-time suggestions appear as you type

###### Advanced Search Panel

1. **Accessing Advanced Search**

   - Click the filter icon next to the search bar
   - The advanced search panel will expand below the search bar
   - Multiple filter criteria can be applied simultaneously

2. **Date Range Filtering**

   - **Start Date**: Select the earliest date for photos to include
   - **End Date**: Select the latest date for photos to include
   - Use date pickers for precise date selection
   - Leave either field empty for open-ended ranges
   - Example: Find all photos from your vacation in July 2024

3. **Location-Based Search**

   - Enter location names, cities, or places
   - Searches through photo location metadata
   - Supports partial matches (e.g., "Paris" will find "Paris, France")
   - Case-insensitive search

4. **Tag-Based Filtering**

   - **Adding Tags**: Type tag names and press Enter or comma
   - **Multiple Tags**: Add multiple tags for more specific searches
   - **Tag Colors**: Each tag gets a unique color for easy identification
   - **Removing Tags**: Click the 'x' on any tag to remove it
   - **Auto-complete**: Suggests existing tags as you type
   - Example: Search for photos tagged with both "family" and "vacation"

5. **People Search**
   - Search for photos containing specific people
   - Enter names or identifiers
   - Useful for finding photos with friends, family members, or colleagues
   - Supports partial name matching

###### Filter Combinations

- **Multiple Criteria**: Use any combination of filters for precise results
- **AND Logic**: All specified criteria must match (photos must meet every condition)
- **Real-time Results**: Photo gallery updates instantly as you apply filters

![advanced-search](final-report-09/advanced-search.png)

##### Organizing with Albums

- Go to the **Albums** section
- Create new albums with custom names and descriptions
- Move photos between albums using the selection tools
- Delete albums (photos will be moved to "Unfiled")

![image-20250526114236434](final-report-09/image-20250526114236434.png)
![albums-view-create](final-report-09/albums-view-create.png)

##### Using the Timeline

- Access the **Timeline** view to browse photos by date
- Scroll through your photo history chronologically

##### Photo Editing

###### Basic Editing

1. Select a photo from any view
2. Click the edit button or double-click the photo
3. Use the built-in editor for:
   - Cropping and resizing
   - Applying filters
   - Adjusting brightness, contrast, and saturation
   - Adding text overlays

![image-20250526114309010](final-report-09/image-20250526114309010.png)

##### AI Style Transfer

1. Navigate to the **Style** section
2. Select photos you want to stylize
3. Choose from available AI style presets
4. Process and download your stylized images

##### Organizing with Tags

1. **Adding Tags**
   - Select photos in any view
   - Use the tag interface to add descriptive keywords
   - Create custom tag categories

![image-20250526114327751](final-report-09/image-20250526114327751.png)

2. **Searching by Tags**
   - Use the search functionality to find photos by tags
   - Filter your photo library using tag combinations

![image-20250526114352764](final-report-09/image-20250526114352764.png)

##### Advanced Features

###### Batch Operations

- Select multiple photos using the selection mode
- Perform bulk operations:
  - Move to albums
  - Download as ZIP
  - Apply tags
  - Delete photos

###### Download and Export

- Download individual photos or entire albums
- Export photos in original quality
- Batch download selected photos as ZIP files

##  Tests

### Tests for java  backend

Technology: jcoco

Test coverage report:http://10.16.60.67/site/jacoco/index.html

![img_1.png](final-report-09/img_1.png)

The low coverage for DTO classes and similar is due to the `@Data` annotation; in reality, they are almost fully covered. Furthermore, the actual request methods are within the controller methods, which are completely covered.

[![img_2.png](final-report-09/img_2.png)](https://github.com/sustech-cs304/team-project-25spring-9/blob/backend-spring/img/img_2.png)

[![img_3.png](final-report-09/img_3.png)

### Tests for python backend

#### 1. Testing Tools and Frameworks

We adopted the following technologies for automated testing:

| Type         | Tool(s)                   | Usage                                                        |
| ------------ | ------------------------- | ------------------------------------------------------------ |
| Unit Testing | `pytest`                  | Test core functions (e.g., `extract_exif_data`, `generate_caption`) |
| API Testing  | `httpx`, `pytest-asyncio` | Send asynchronous requests to FastAPI endpoints              |
| Coverage     | `coverage.py`             | Display code coverage metrics                                |

------

#### 2. Test Structure

You may add a `tests/test_api.py` file like this:

```python
import pytest
from fastapi.testclient import TestClient
from process_image import app

client = TestClient(app)

def test_extract_exif_api():
    with open("tests/sample.jpg", "rb") as f:
        response = client.post("/extract_exif/", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    assert "Timestamp" in response.json()   

def test_generate_caption():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/generate_caption/", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    assert "caption" in response.json()


def test_auto_tag():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/auto_tag/", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    assert "tags" in response.json()
    assert isinstance(response.json()["tags"], list)


def test_process_image():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/process_image", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    data = response.json()
    assert "Caption" in data
    assert "AutoTags" in data
    assert "PersonLabel" in data
```

------

#### 3. Test Coverage Report

```bash
pip install coverage
coverage run -m pytest
coverage report -m
```

**Output:**

```
Name                  Stmts   Miss  Cover
-----------------------------------------
process_image.py       804     94    88%
-----------------------------------------
```

**Test coverage above 85% is considered good.**





## Automated Build and Continuous Integration

To ensure maintainability, reproducibility, and deployment efficiency, we implemented an automated build pipeline using **Jenkins** in combination with **Docker**. This setup allows us to compile, assemble, and deploy our project in a fully automated manner, without manual intervention. Below is a detailed explanation of the technologies and steps involved.

### 1. Technologies and Tools Used

- **Jenkins**: A widely-used automation server for continuous integration and deployment.
- **Docker / Docker Compose**: Containerization tools used to isolate project services and guarantee consistent runtime environments.
- **Shell Scripts**: Used in the pipeline to execute build and deployment commands.
- **Git**: Source code management and version control.
- *(Optional for future extension)*: Linters, unit testing frameworks, and documentation tools.

### 2. Tasks Executed in the Build Process

The Jenkins pipeline is divided into multiple stages, each responsible for a specific task in the build process:

1. **Stop Old Containers**
   - Ensures a clean environment by shutting down previously running containers.
   - Command: `docker compose down || true`
2. **Build Service Images**
   - Builds Docker images for all services defined in the `docker-compose.yml` file.
   - Command: `docker compose build`
3. **Start Services**
   - Starts all services in detached mode.
   - Command: `docker compose up -d`
4. **Verify Service Status**
   - Displays the running status of all containers.
   - Command: `docker compose ps`
5. **Post-Build Actions**
   - On success: Displays "✅ Deployment successful!"
   - On failure: Outputs "❌ Build failed, please check the Jenkins console logs."

> **Note:** The build process can be extended to include:
>
> - **Code Linting** (e.g., ESLint, Pylint)
> - **Automated Testing and Coverage Reports** (e.g., pytest, JUnit, Jest)
> - **Documentation Generation** (e.g., Sphinx, JSDoc)

### 3. Build Artifacts

![image-20250525233031178](final-report-09/image-20250525233031178.png)

![image-20250525233052904](final-report-09/image-20250525233052904.png)

Upon a successful build, the following artifacts are generated:

- **Latest Docker images** containing the compiled code and dependencies.
- **Running container instances** of the project services.
- **Build logs** viewable through Jenkins.
- *(Optional)* Test reports, documentation HTML files, etc.

These artifacts can be used directly for deployment to staging or production environments.

### 4. Build Configuration Files

The build and deployment process is managed using the following key files:

#### `docker-compose.yml`

Defines all project services, including build context, dependencies, and network configuration. *(Provide link or snippet as needed.)*

#### `Jenkinsfile`

```groovy
pipeline {
  agent any
  environment {
    COMPOSE_FILE = 'docker-compose.yml'
  }

  stages {
    stage('Stop Old Containers') {
      steps { sh 'docker compose down || true' }
    }
    stage('Build All Service Images') {
      steps { sh 'docker compose build' }
    }
    stage('Start All Services') {
      steps { sh 'docker compose up -d' }
    }
    stage('Verify Service Status') {
      steps { sh 'docker compose ps' }
    }
  }

  post {
    success { echo '✅ Deployment successful!' }
    failure { echo '❌ Build failed, please check the Jenkins console logs.' }
  }
}
```

### 5. Summary

By integrating Jenkins and Docker, we established a streamlined, reliable, and reproducible build process that enhances productivity and ensures consistent deployment across environments. This CI/CD pipeline lays the foundation for future enhancements, such as automated testing, static analysis, and documentation, which can be easily integrated to further improve code quality and development workflow.

------



## Deployment

### 1.Technology/Tools/Frameworks/Approaches Used for Containerization

Our project leverages **Docker** for individual service containerization and **Docker Compose** for orchestrating and managing the multi-container application. This approach offers several benefits:

- **Isolation:** Each service (backend, frontend, image processing, database, object storage) runs in its own isolated container, preventing conflicts and ensuring consistent environments.
- **Portability:** The containerized application can be easily deployed across different environments (development, testing, production) without compatibility issues.
- **Scalability:** Individual services can be scaled independently as needed.
- **Simplified Deployment:** Docker Compose streamlines the deployment process by defining all services, networks, and volumes in a single configuration file.

The key technologies and tools used are:

- **Docker Engine:** The core platform for building, running, and managing containers.
- **Docker Compose:** A tool for defining and running multi-container Docker applications.
- **Dockerfiles:** Used to define the build steps for each custom service image (backend, frontend, image processing).
- **Spring Boot:** Framework for the backend application.
- **Vue.js/Vite:** Framework for the frontend application.
- **Python:** Used for the `Process_Image` application.
- **MySQL:** Relational database.
- **MinIO:** S3-compatible object storage.

### 2.Script or Related Artifacts Used for Containerization

The primary artifact for containerization is the `docker-compose.yml` file, which defines the entire multi-service application. Individual Dockerfiles within the `backend`, `frontend`, and `Process_Image` directories are used to build the specific images for those services.

Here's the `docker-compose.yml` content:

```dockerfile
version: '3.8'

services:
  backend-app:
    build: ./backend
    container_name: my-backend-app-together-1
    ports:
      - "9090:9091"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    restart: unless-stopped

  web:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      target: production
    image: album:latest
    ports:
      - "0.0.0.0:5173:5173"
    restart: unless-stopped
    container_name: my-frontend-app-1

  process-image-app:
    build: ./Process_Image
    ports:
      - "8123:8123"
    volumes:
      - ./Process_Image:/app
    environment:
      - PYTHONUNBUFFERED=1
    container_name: my-process-image-app-1
    restart: unless-stopped

  mysql:
    image: mysql:latest
    container_name: software_eng_mysql_together-1
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: software_eng_pss
      MYSQL_DATABASE: software_eng
      MYSQL_USER: root_1
      MYSQL_PASSWORD: software_eng_pss
    ports:
      - "3306:3306"
    volumes:
      - /var/lib/mysql/software_eng_data:/var/lib/mysql
      - ./backend/table.sql:/docker-entrypoint-initdb.d/table.sql

  minio:
    image: minio/minio:latest
    container_name: software_eng_minio_together-1
    restart: always
    environment:
      MINIO_ROOT_USER: root
      MINIO_ROOT_PASSWORD: software_eng_pss
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - /var/lib/minio/data:/data
    command: server /data --console-address ":9001"

  createbuckets:
    image: minio/mc
    container_name: software_eng_minio_createbuckets-1
    depends_on:
      - minio
    entrypoint: >
      /bin/sh -c "
      echo 'Waiting for MinIO to be ready...';
      sleep 10;
      echo 'Setting up MinIO alias...';
      /usr/bin/mc alias set local http://minio:9000 root software_eng_pss;
      echo 'Creating buckets...';
      /usr/bin/mc mb local/softwareeng;
      /usr/bin/mc anonymous set public local/softwareeng/;
      /usr/bin/mc mb local/softwareeng/user-img;
      /usr/bin/mc mb local/softwareeng/builder-img;
      /usr/bin/mc mb local/softwareeng/comment-img;
      /usr/bin/mc mb local/softwareeng/restaurant-img;
      /usr/bin/mc mb local/softwareeng/food-img;
      /usr/bin/mc mb local/softwareeng/bus-json;
      /usr/bin/mc mb local/softwareeng/commodity-img;
      /usr/bin/mc mb local/softwareeng/upload-img;
      echo 'Bucket creation script finished.';
      exit 0;
      "
```



### 3.Proof of Successful Containerization

Containerization result:

![image-20250525162622349](final-report-09/image-20250525162622349.png)

The proof video of deployment: http://10.16.60.67/site/docker-build-speedup.mp4





