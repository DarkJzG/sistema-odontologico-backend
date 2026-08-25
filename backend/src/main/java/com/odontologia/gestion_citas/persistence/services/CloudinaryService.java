//src/main/java/com/odontologia/gestion_citas/persistence/services/CloudinaryService.java

package com.odontologia.gestion_citas.persistence.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    public String subirImagen(MultipartFile file) throws IOException {
        // Sube el archivo y lo guarda en una carpeta virtual llamada "odontostyle"
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "odontostyle"));
        // Retorna la URL pública y segura de la imagen
        return uploadResult.get("secure_url").toString();
    }
}