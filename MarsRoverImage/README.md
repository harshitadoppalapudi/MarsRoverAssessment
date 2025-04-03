# 🚀 NASA Mars Rover Image Downloader

This Java application downloads and stores Mars Rover images from specific dates using NASA's Mars Rover Photos API.

## 🌟 Features

- 📅 Reads dates from a text file in multiple formats.
- 🔍 Queries the **NASA Mars Rover API** for images taken on those dates.
- 📥 Downloads and stores the images locally.
- 🗓️ Handles different date formats (`MM/dd/yy`, `MMMM d, yyyy`, `MMM-dd-yyyy`).
- ⚠️ Includes **error handling** for invalid dates and API issues.
- 🐳 **Docker support** for easy deployment.

---

## 📌 Prerequisites

Ensure you have the following installed:

- ☕ **Java 11** or higher (for running the application).
- 🛠️ **Maven 3.8.6** or higher (only needed for building from source).
- 🐳 **Docker** (optional, for containerized deployment).
- 🌐 An **internet connection**.
- 🔑 *(Optional)* **NASA API key** for higher request limits.

---

## 📥 Getting Started

### 🔹 Clone the Repository

```bash
git clone https://github.com/harshitadoppalapudi/MarsRoverAssessment.git
cd nasa-rover-image-downloader
```

---

# ⚙️ Building the Project from Scratch

### ✅ Using Maven

1. **Build the application**  
   ```bash
   mvn clean package
   ```
2. **Run tests**  
   ```bash
   mvn test
   ```
3. **Run the application**  
   ```bash
   java -jar target/nasa-rover-image-downloader-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```
4. *(Optional)* **Run with NASA API key**  
   ```bash
   NASA_API_KEY=your_api_key java -jar target/nasa-rover-image-downloader-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

---

### 🐳 Without Maven (Using Docker)

1. Ensure **Docker** is installed and running in the background.
2. **Build the Docker image**  
   ```bash
   docker build -t nasa-rover-app .
   ```
3. **Run tests**  
   ```bash
   .\run_tests.bat
   ```
4. **Run the Docker container**  
   ```bash
   docker run -e NASA_API_KEY=your_api_key -v ${PWD}/nasa_images:/app/nasa_images nasa-rover-app
   ```

---

# 📦 Docker Deployment

### 🛠️ Using Docker Directly

1. **Build the Docker image**  
   ```bash
   docker build -t nasa-rover-app .
   ```
2. **Run the Docker container**  
   ```bash
   docker run -e NASA_API_KEY=your_api_key -v ${PWD}/nasa_images:/app/nasa_images nasa-rover-app
   ```

---

### 📜 Using Docker Compose

1. *(Optional)* Edit `docker-compose.yml` to set your **NASA API key** and any other configurations.
2. **Build and run the container**  
   ```bash
   docker-compose up --build
   ```
3. **To run in detached mode**  
   ```bash
   docker-compose up -d
   ```
4. **To stop the container**  
   ```bash
   docker-compose down
   ```

---

## 🔧 Docker Configuration Options

- **NASA API Key**: Set the `NASA_API_KEY` environment variable to use your own NASA API key.
- **Date File**: Mount a custom `dates.txt` file by adjusting the relevant line in `docker-compose.yml`.
- **Image Storage**: By default, images are stored in the mounted `./nasa_images` directory.

---

# 🎯 Running Without Maven (Pre-built JAR)

If you received a **pre-built ZIP file**, you can run it without installing Maven:

### 🛠️ Steps:
1. **Extract the ZIP file** to a directory of your choice.
2. **Open a terminal/command prompt**.
3. **Navigate to the directory containing the JAR file**.
4. **Run the application**  
   ```bash
   java -jar nasa-rover-image-downloader-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```
5. *(Optional)* **Run with NASA API key**  
   ```bash
   NASA_API_KEY=your_api_key java -jar nasa-rover-image-downloader-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

📂 The application will download **Mars Rover images** for the dates specified in `dates.txt` and store them in the `nasa_images` folder.

---

## ✅ Running Tests Manually

- **With Maven**  
  ```bash
  mvn test
  ```
- **Without Maven (Using Docker)**  
  ```bash
  .\run_tests.bat
  ```

---
