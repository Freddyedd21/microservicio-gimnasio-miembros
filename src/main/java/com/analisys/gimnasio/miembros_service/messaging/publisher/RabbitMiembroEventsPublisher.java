package com.analisys.gimnasio.miembros_service.messaging.publisher;

import java.time.Instant;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.analisys.gimnasio.miembros_service.messaging.event.MiembroCreadoEvent;
import com.analisys.gimnasio.miembros_service.messaging.event.InscripcionCreadaEvent;
import com.analisys.gimnasio.miembros_service.model.Miembro;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMiembroEventsPublisher implements MiembroEventsPublisher {

    public static final String ROUTING_KEY_MIEMBRO_CREADO = "miembro.evento.creado";
    public static final String ROUTING_KEY_INSCRIPCION_CREADA = "inscripcion.creada";

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    @Override
    public void publishMiembroCreado(Miembro miembro) {
        MiembroCreadoEvent event = new MiembroCreadoEvent(
                miembro.getId(),
                miembro.getNombre(),
                miembro.getEmail(),
                miembro.getFechaInscripcion(),
                miembro.getEntrenadorPersonalId(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(topicExchange.getName(), ROUTING_KEY_MIEMBRO_CREADO, event);
    }

    @Override
    public void publishInscripcionCreada(Miembro miembro) {
        InscripcionCreadaEvent event = new InscripcionCreadaEvent(
                miembro.getId(),
                miembro.getNombre(),
                miembro.getEmail(),
                miembro.getFechaInscripcion(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(topicExchange.getName(), ROUTING_KEY_INSCRIPCION_CREADA, event);
    }
}
