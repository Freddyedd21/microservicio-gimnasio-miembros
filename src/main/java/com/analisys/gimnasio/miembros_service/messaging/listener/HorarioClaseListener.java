package com.analisys.gimnasio.miembros_service.messaging.listener;

import com.analisys.gimnasio.miembros_service.config.RabbitMQConfig;
import com.analisys.gimnasio.miembros_service.messaging.event.HorarioClaseCambiadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HorarioClaseListener {

    private static final Logger log = LoggerFactory.getLogger(HorarioClaseListener.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_MIEMBROS_HORARIO)
    public void onHorarioClaseCambiado(HorarioClaseCambiadoEvent event) {
        log.info(
                "[Miembros] Horario clase cambiado: claseId={}, anterior={}, nuevo={}, occurredAt={}",
                event.getClaseId(),
                event.getHorarioAnterior(),
                event.getHorarioNuevo(),
                event.getOccurredAt()
        );
    }
}
