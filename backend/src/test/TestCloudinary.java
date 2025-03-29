package test;

import com.covera.config.CloudinaryConfig;

public class TestCloudinary {
    public static void main(String[] args) {
        CloudinaryConfig.connectCloudinary();
        System.out.println("Cloudinary connection test successful! ✅");
    }
}
