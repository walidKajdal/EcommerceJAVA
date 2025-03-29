package com.covera.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

public class CloudinaryConfig {
    private static Cloudinary cloudinary;

    public static void connectCloudinary() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_SECRET_KEY")
        ));
        System.out.println("✅ Cloudinary connected successfully!");
    }

    public static Cloudinary getCloudinary() {
        return cloudinary;
    }
}
