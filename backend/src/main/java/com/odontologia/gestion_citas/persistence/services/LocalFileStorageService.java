//src/main/java/com/odontologia/gestion_citas/persistence/services/LocalFileStorageService.java
package com.odontologia.gestion_citas.persistence.services;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements StorageService {
    
    private final Path rootLocation = Paths.get("uploads");

    public LocalFileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la carpeta de archivos locales.");
        }
    }

    @Override
    public String guardarArchivo(MultipartFile archivo, String carpetaDestino) {
        try {
            // Generamos un nombre único para que no se sobreescriban radiografías con el mismo nombre
            String nombreUnico = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path destino = this.rootLocation.resolve(nombreUnico);
            
            // Copiamos el archivo a la carpeta local
            Files.copy(archivo.getInputStream(), destino);

            // Devolvemos la URL local para que Angular pueda consumirla
            // (Esta URL apuntará a un controlador que crearemos para servir imágenes)
            return "http://localhost:8081/api/archivos/" + nombreUnico;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo localmente.");
        }
    }
}