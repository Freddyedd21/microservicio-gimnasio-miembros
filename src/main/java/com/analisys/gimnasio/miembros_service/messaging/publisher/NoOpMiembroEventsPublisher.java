package com.analisys.gimnasio.miembros_service.messaging.publisher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.analisys.gimnasio.miembros_service.model.Miembro;

@Service
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "false")
public class NoOpMiembroEventsPublisher implements MiembroEventsPublisher {

    @Override
    public void publishMiembroCreado(Miembro miembro) {
        // Intencionalmente vacío: usado para tests o cuando se deshabilita RabbitMQ.
    }
}
