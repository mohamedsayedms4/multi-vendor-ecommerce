package org.example.ecommerce.infrastructure.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageUploadUtil {
    private static final String UPLOAD_DIR = "C:/Users/Mohamed/Music/New folder/";

    /**
     * Save uploaded images to the server folder and return their relative paths.
     */


    private final Cloudinary cloudinary;

    /**
     * Save uploaded images to Cloudinary and return their URLs.
     */
    public List<String> saveImages(MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();

        try {
            for (MultipartFile file : images) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("folder", "remotly_ecommerce"));
                String url = (String) uploadResult.get("secure_url");
                imageUrls.add(url);
            }
        } catch (IOException e) {
            log.error("Failed to upload images to Cloudinary", e);
            throw new RuntimeException("Failed to upload images", e);
        }

        return imageUrls;
    }
    /**
     * Save a single image to Cloudinary and return its URL.
     *
     * @param image MultipartFile representing the image
     * @return URL of the uploaded image
     */
    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return saveImages(new MultipartFile[]{image}).get(0);
    }

}
