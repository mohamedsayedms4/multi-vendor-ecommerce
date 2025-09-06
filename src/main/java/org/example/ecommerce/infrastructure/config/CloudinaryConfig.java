package org.example.ecommerce.infrastructure.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnfdezpod",
                "api_key", "932764322994644",
                "api_secret", "joeNHyv_kogJ_szwjzb3xftm1ps"
        ));
    }
}
