### Project Overview -> **Java JDBC Online Gallery Management System**

To learn JDBC in detail then [click here](./the-md-files/1%20all-about-JDBC.md).

This project is a console-based application that allows users to:
1. **Register**: A user can register with a username and password.
2. **Login**: A user can log in with their credentials.
3. **Upload Images**: After logging in, a user can upload images with a description.
4. **View Uploaded Images**: Users can view the list of uploaded images (image metadata).
5. **Download Images**: Users can download an image from the database to their local system.

The project consists of the following components:
- **Main Application (`GalleryApp.java`)**: This is the entry point and contains the console menu to interact with the system.
- **User Model (`User.java`)**: Represents the user entity.
- **Image Model (`Image.java`)**: Represents the image entity.
- **UserService (`UserService.java`)**: Contains logic to manage users (registration, authentication).
- **ImageService (`ImageService.java`)**: Contains logic to manage image uploads, viewing, and downloading.
- **Database Utility (`DatabaseUtil.java`)**: Handles the connection to the MySQL database.

### This is the complete project structure:
- **Project Structure**:
  ```
  JDBC-OnlineGalleryManagementSystem/
  ├── src/
  │   ├── db/
  │   │   └── DatabaseUtil.java
  │   ├── model/
  │   │   ├── User.java
  │   │   └── Image.java
  │   ├── service/
  │   │   ├── UserService.java
  │   │   └── ImageService.java
  │   ├── main/
  │   │   └── GalleryApp.java
  └── lib/
      └── mysql-connector-java.jar (or your preferred JDBC driver)
  ```

### Detailed Explanation

#### 1. `User.java` (Model)
The `User` class represents the user in the system. It contains fields for `userId`, `username`, and `password`.

```java
package model;

public class User {
    private int userId;
    private String username;
    private String password;

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

**Purpose**: This class is used to store and retrieve user data in the application. It acts as a data carrier for user-related operations.

#### 2. `Image.java` (Model)
The `Image` class represents the image that will be uploaded, including the image file path, description, and the user who uploaded it.

```java
package model;

public class Image {
    private int imageId;
    private int userId;
    private String imageName;
    private String description;

    // Getters and Setters
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

**Purpose**: This class is used to represent an image, including its name, associated user, and description. It acts as a data carrier for image-related operations.

#### 3. `UserService.java` (Service)
This class handles user-related functionalities such as user registration and login (authentication). It interacts with the database to perform these actions.

```java
package service;

import db.DatabaseUtil;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    // Method to register a user
    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());  // In a real system, password should be hashed
            pstmt.executeUpdate();
        }
    }

    // Method to authenticate a user (login)
    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // In a real system, this should compare hashed passwords
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Returns true if user exists
        }
    }
}
```

- **registerUser(User user)**: Registers a new user by inserting their username and password into the `users` table.
- **authenticateUser(String username, String password)**: Authenticates a user by checking the username and password in the database.

#### 4. `ImageService.java` (Service)
This class handles image-related functionalities like uploading, viewing, and downloading images.

```java
package service;

import db.DatabaseUtil;
import model.Image;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ImageService {

    // Method to upload an image to the database
    public void uploadImage(Image image) throws SQLException, IOException {
        String sql = "INSERT INTO images (user_id, image_name, image_data, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(image.getImageName())) {
             
            File file = new File(image.getImageName());
            String fileName = file.getName();  // Only store the image name, not full path
            
            pstmt.setInt(1, image.getUserId());
            pstmt.setString(2, fileName);  // Store the image name in the database
            pstmt.setBlob(3, fis);  // Store the binary data (image) in the database
            pstmt.setString(4, image.getDescription());
            pstmt.executeUpdate();
        }
    }

    // Method to view images metadata (without the actual image)
    public void viewImages() throws SQLException {
        String sql = "SELECT * FROM images";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Image ID: " + rs.getInt("image_id"));
                System.out.println("Image Name: " + rs.getString("image_name"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Upload Date: " + rs.getTimestamp("upload_date"));
                System.out.println("---------------------------");
            }
        }
    }

    // Method to download an image from the database
    public void downloadImage(int imageId, String outputFilePath) throws SQLException, IOException {
        String sql = "SELECT image_data FROM images WHERE image_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, imageId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Blob blob = rs.getBlob("image_data");
                byte[] imageBytes = blob.getBytes(1, (int) blob.length());

                // Save the image to the local file system
                try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                    fos.write(imageBytes);
                }
                System.out.println("Image downloaded successfully at: " + outputFilePath);
            } else {
                System.out.println("No image found with ID: " + imageId);
            }
        }
    }
}
```

- **uploadImage(Image image)**: Uploads an image along with its description into the database.
- **viewImages()**: Displays the list of uploaded images (image ID, name, description).
- **downloadImage(int imageId, String outputFilePath)**: Downloads an image from the database and saves it to the local file system.

#### 5. `DatabaseUtil.java` (Utility Class)
This class handles the database connection using the JDBC API.

```java
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    
    private static final String URL = "jdbc:mysql://localhost:3306/online_gallery";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Method to get the database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

- **getConnection()**: Establishes and returns a connection to the MySQL database.

#### 6. `GalleryApp.java` (Main Application)
This is the entry point of the application, handling user interactions through the console. It uses `UserService` for user management and `ImageService` for image operations.

```java
package main;

import model.Image;
import model.User;
import service.ImageService;
import service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class GalleryApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ImageService imageService = new ImageService

();

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> {
                    System.out.println("Exiting the application.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        try {
            userService.registerUser(user);
            System.out.println("Registration successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error during registration.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            if (userService.authenticateUser(username, password)) {
                System.out.println("Login successful!");
                userDashboard();
            } else {
                System.out.println("Invalid credentials. Try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error during login.");
        }
    }

    private static void userDashboard() {
        while (true) {
            System.out.println("1. Upload Image");
            System.out.println("2. View Images");
            System.out.println("3. Download Image");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1 -> uploadImage();
                case 2 -> viewImages();
                case 3 -> downloadImage();
                case 4 -> {
                    System.out.println("Logging out.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void uploadImage() {
        System.out.print("Enter image file path: ");
        String imagePath = scanner.nextLine();
        System.out.print("Enter image description: ");
        String description = scanner.nextLine();

        Image image = new Image();
        image.setImageName(imagePath);
        image.setDescription(description);

        try {
            imageService.uploadImage(image);
            System.out.println("Image uploaded successfully!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Error uploading image.");
        }
    }

    private static void viewImages() {
        try {
            imageService.viewImages();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving images.");
        }
    }

    private static void downloadImage() {
        System.out.print("Enter the image ID to download: ");
        int imageId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter the file path to save the image (e.g., C:/Users/Mayank/Desktop/image.jpg): ");
        String outputFilePath = scanner.nextLine();

        try {
            imageService.downloadImage(imageId, outputFilePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Error downloading image.");
        }
    }
}
```

### Key Operations:
1. **Registration**: The `registerUser()` method takes user input, creates a `User` object, and uses `userService` to register the user in the database.
2. **Login**: The `loginUser()` method takes username and password as input, and `userService` authenticates the user by verifying the credentials from the database.
3. **Upload Image**: The `uploadImage()` method takes an image file path and description from the user, creates an `Image` object, and uses `imageService` to store the image in the database.
4. **View Images**: The `viewImages()` method lists all uploaded images from the database by displaying their metadata (image ID, name, description, and upload date).
5. **Download Image**: The `downloadImage()` method allows the user to specify an image ID and a file path, and the image is retrieved from the database and saved locally.

---

### Database Schema

Here is the schema for the MySQL tables used:

1. **Users Table**:
   ```sql
   CREATE TABLE users (
       user_id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(255) NOT NULL,
       password VARCHAR(255) NOT NULL
   );
   ```

2. **Images Table**:
   ```sql
   CREATE TABLE images (
       image_id INT AUTO_INCREMENT PRIMARY KEY,
       user_id INT,
       image_name VARCHAR(500),  -- Increased size to accommodate long file names
       image_data LONGBLOB,      -- To store the binary image data
       description VARCHAR(500),
       upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(user_id)
   );
   ```

---

### Conclusion

In this project:
- We used **Java JDBC** to connect to a MySQL database.
- We implemented **user authentication** and **CRUD operations** for images.
- Images are stored as binary data (`BLOB`).
- We used **console-based input/output** for user interaction.
