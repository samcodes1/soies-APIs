package com.rtechnologies.soies.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary getCloudinary(){
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dm7uq1adt",
                "api_key", "246721327163695",
                "api_secret", "Pozr913oXcnPC6P4JrsSjkuA6oA",
                "secure", true));

        return cloudinary;
    }
}
