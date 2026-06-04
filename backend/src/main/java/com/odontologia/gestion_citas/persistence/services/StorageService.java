//src/main/java/com/odontologia/gestion_citas/persistence/services/StorageService.java

package com.odontologia.gestion_citas.persistence.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    // Recibe el archivo y devuelve la URL (local o de la nube) donde quedó guardado
    String guardarArchivo(MultipartFile archivo, String carpetaDestino);
}