// src/main/java/com/odontologia/gestion_citas/persistence/services/NotificacionWhatsAppService.java
package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.persistence.entities.Cita;
import com.odontologia.gestion_citas.persistence.entities.ConfiguracionWhatsapp;
import com.odontologia.gestion_citas.persistence.repositories.CitaRepository;
import com.odontologia.gestion_citas.persistence.repositories.ConfiguracionWhatsappRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificacionWhatsAppService {

    private final CitaRepository citaRepository;
    private final ConfiguracionWhatsappRepository configRepo;
    
    // Herramienta de Spring para hacer peticiones a otras APIs
    private final RestTemplate restTemplate = new RestTemplate();
    
    // La URL de tu nuevo microservicio Node.js
    private final String WHATSAPP_API_URL = "http://localhost:3000/api/whatsapp/enviar";

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void procesarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        
        LocalDateTime en24HorasInicio = ahora.plusHours(24).withSecond(0).withNano(0);
        LocalDateTime en24HorasFin = en24HorasInicio.plusMinutes(1);

        LocalDateTime en1HoraInicio = ahora.plusHours(1).withSecond(0).withNano(0);
        LocalDateTime en1HoraFin = en1HoraInicio.plusMinutes(1);

        enviarRecordatorios24Horas(en24HorasInicio, en24HorasFin);
        enviarRecordatorios1Hora(en1HoraInicio, en1HoraFin);
    }

    private void enviarRecordatorios24Horas(LocalDateTime inicio, LocalDateTime fin) {
        List<Cita> citas = citaRepository.buscarCitasParaRecordatorio(
                Cita.EstadoCita.PENDIENTE, inicio, fin, false);

        ConfiguracionWhatsapp config = configRepo.findById(1).orElse(new ConfiguracionWhatsapp());

        for (Cita cita : citas) {
            String telefono = formatearTelefono(cita.getPaciente().getTelefono());
            
            // Reemplazo dinámico
            String mensaje = config.getMensaje24h()
                    .replace("[NOMBRE_PACIENTE]", cita.getPaciente().getNombres())
                    .replace("[HORA_CITA]", cita.getFechaHoraInicio().toLocalTime().toString());

            enviarMensajeHTTP(telefono, mensaje);
            cita.setRecordatorioDiaEnviado(true);
            citaRepository.save(cita);
        }
    }

    private void enviarRecordatorios1Hora(LocalDateTime inicio, LocalDateTime fin) {
        // AQUÍ EL CAMBIO: Ya no le pasamos el "true", solo le pasamos el "false" de la hora
        List<Cita> citas = citaRepository.buscarCitasParaRecordatorioHora(
                Cita.EstadoCita.PENDIENTE, inicio, fin, false);

        ConfiguracionWhatsapp config = configRepo.findById(1).orElse(new ConfiguracionWhatsapp());

        for (Cita cita : citas) {
            String telefono = formatearTelefono(cita.getPaciente().getTelefono());
            
            String mensaje = config.getMensaje1h()
                    .replace("[NOMBRE_PACIENTE]", cita.getPaciente().getNombres())
                    .replace("[HORA_CITA]", cita.getFechaHoraInicio().toLocalTime().toString());

            enviarMensajeHTTP(telefono, mensaje);
            cita.setRecordatorioHoraEnviado(true);
            citaRepository.save(cita);
        }
    }

    // Método que ejecuta el POST hacia Node.js
    private void enviarMensajeHTTP(String telefono, String mensaje) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("telefono", telefono);
            body.put("mensaje", mensaje);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            
            // Disparamos la petición
            restTemplate.postForEntity(WHATSAPP_API_URL, request, String.class);
            
        } catch (Exception e) {
            System.err.println("Fallo al conectar con el microservicio de WhatsApp: " + e.getMessage());
        }
    }

    private String formatearTelefono(String telefonoLocal) {
        if (telefonoLocal == null || telefonoLocal.isEmpty()) return "";
        String limpio = telefonoLocal.replaceAll("\\s+", "");
        if (limpio.startsWith("0")) {
            return "+593" + limpio.substring(1);
        } else if (!limpio.startsWith("+593")) {
            return "+593" + limpio;
        }
        return limpio;
    }
}