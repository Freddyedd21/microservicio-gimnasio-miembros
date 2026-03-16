package com.analisys.gimnasio.miembros_service.kafka;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatosEntrenamientoProducer {

    private final KafkaTemplate<String, DatosEntrenamiento> kafkaTemplate;

    public DatosEntrenamientoProducer(KafkaTemplate<String, DatosEntrenamiento> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarDatoEntrenamiento(String miembroId, String tipo, int duracionMinutos, int calorias) {
        DatosEntrenamiento evento = new DatosEntrenamiento(
            miembroId,
            tipo,
            duracionMinutos,
            calorias,
            LocalDateTime.now()
        );
        kafkaTemplate.send("datos-entrenamiento", miembroId, evento);
    }
}
