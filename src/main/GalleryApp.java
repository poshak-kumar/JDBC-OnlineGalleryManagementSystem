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
    private static final ImageService imageService = new ImageService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
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
        user.setPassword(password); // Ideally, hash this password

        try {
            userService.registerUser(user);
            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error registering user.");
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
                userDashboard(username);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error during login.");
        }
    }

    private static void userDashboard(String username) {
        int userId = 1; // Hardcoded user ID for simplicity. Fetch this from database based on username.

        while (true) {
            System.out.println("1. Upload Image");
            System.out.println("2. View Images");
            System.out.println("3. Download Image");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    uploadImage(userId);
                    break;
                case 2:
                    viewImages();
                    break;
                case 3:
                    downloadImage();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void uploadImage(int userId) {
        System.out.print("Enter image file path: ");
        String imagePath = scanner.nextLine();
        System.out.print("Enter image description: ");
        String description = scanner.nextLine();

        Image image = new Image();
        image.setUserId(userId);
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
        scanner.nextLine(); // Consume newline

        System.out.print("Enter the file path to save the image (e.g., C:/Users/Mayank/Desktop/image.jpg): ");
        String outputPath = scanner.nextLine();

        try {
            imageService.downloadImage(imageId, outputPath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Error downloading image.");
        }
    }
}
