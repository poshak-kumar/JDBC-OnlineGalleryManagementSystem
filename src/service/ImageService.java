package service;

import db.DatabaseUtil;
import model.Image;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ImageService {
    
    // Upload image (this method remains unchanged)
    public void uploadImage(Image image) throws SQLException, IOException {
        String sql = "INSERT INTO images (user_id, image_name, image_data, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(image.getImageName())) {
            pstmt.setInt(1, image.getUserId());
            pstmt.setString(2, image.getImageName());
            pstmt.setBlob(3, fis);
            pstmt.setString(4, image.getDescription());
            pstmt.executeUpdate();
        }
    }

    // Method to view all images' metadata (image name, description, etc.)
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

    // New method to download and save the image locally
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
