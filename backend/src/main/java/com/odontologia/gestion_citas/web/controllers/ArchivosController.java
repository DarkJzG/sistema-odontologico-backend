// src/main/java/com/odontologia/gestion_citas/web/controllers/ArchivosController.java
package com.odontologia.gestion_citas.web.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/archivos")
public class ArchivosController {

    private final Path rootLocation = Paths.get("uploads");

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> servirArchivo(@PathVariable("nombreArchivo") String nombreArchivo) {
        try {
            Path archivoPath = rootLocation.resolve(nombreArchivo);
            Resource recurso = new UrlResource(archivoPath.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                // Detectamos dinámicamente si es un png o jpg para que el navegador lo muestre en lugar de descargarlo
                String contentType = nombreArchivo.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                        .body(recurso);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}