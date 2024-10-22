package model;

import java.sql.Blob;
import java.util.Date;

public class Image {
    private int imageId;
    private int userId;
    private String imageName;
    private Blob imageData;
    private String description;
    private Date uploadDate;

    // Getters and Setters

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public Blob getImageData() {
        return imageData;
    }
    public void setImageData(Blob imageData) {
        this.imageData = imageData;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
