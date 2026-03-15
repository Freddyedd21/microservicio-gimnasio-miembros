package com.analisys.gimnasio.miembros_service.messaging.publisher;

import com.analisys.gimnasio.miembros_service.model.Miembro;

public interface MiembroEventsPublisher {

    void publishMiembroCreado(Miembro miembro);
}
