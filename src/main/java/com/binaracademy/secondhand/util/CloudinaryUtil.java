package com.binaracademy.secondhand.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUtil {
    private CloudinaryUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
		"cloud_name", "anndev",
		"api_key", "396318962494873",
		"api_secret", "z0tmVczxIVVEzSFP54PerPEZObM",
		"secure", true));
}
