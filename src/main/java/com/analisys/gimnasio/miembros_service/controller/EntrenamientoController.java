package com.analisys.gimnasio.miembros_service.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.analisys.gimnasio.miembros_service.dto.DatosEntrenamientoRequest;
import com.analisys.gimnasio.miembros_service.kafka.DatosEntrenamientoProducer;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.recovery.RecuperacionService;
import com.analisys.gimnasio.miembros_service.service.MiembroService;
import com.analisys.gimnasio.miembros_service.stream.ResumenEntrenamiento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/entrenamientos")
@Tag(name = "Entrenamientos", description = "Gestión de datos de entrenamiento y recuperación Kafka")
public class EntrenamientoController {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final MiembroService miembroService;
    private final DatosEntrenamientoProducer producer;
    private final RecuperacionService recuperacionService;

    public EntrenamientoController(
        MiembroService miembroService,
        DatosEntrenamientoProducer producer,
        RecuperacionService recuperacionService,
        StreamsBuilderFactoryBean streamsBuilderFactoryBean
    ) {
        this.miembroService = miembroService;
        this.producer = producer;
        this.recuperacionService = recuperacionService;
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    @Operation(
        summary = "Registrar entrenamiento",
        description = "Publica los datos de entrenamiento de un miembro en el topic 'datos-entrenamiento' de Kafka"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dato enviado correctamente a Kafka",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "mensaje": "Dato de entrenamiento enviado a Kafka",
                      "miembroId": 1
                    }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Miembro no encontrado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "error": "Miembro no encontrado con ID: 99"
                    }
                """)
            )
        )
    })
    @PostMapping("/{miembroId}")
    public ResponseEntity<Map<String, Object>> registrarEntrenamiento(
        @Parameter(description = "ID del miembro", example = "1", required = true)
        @PathVariable Long miembroId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del entrenamiento a registrar",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "tipoEntrenamiento": "Cardio",
                      "duracionMinutos": 45,
                      "calorias": 300
                    }
                """)
            )
        )
        @RequestBody DatosEntrenamientoRequest request
    ) {
        Miembro miembro = miembroService.obtenerMiembroPorId(miembroId);
        if (miembro == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Miembro no encontrado con ID: " + miembroId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        producer.publicarDatoEntrenamiento(
            String.valueOf(miembroId),
            request.getTipoEntrenamiento(),
            request.getDuracionMinutos(),
            request.getCalorias()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Dato de entrenamiento enviado a Kafka");
        response.put("miembroId", miembroId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener resumen de entrenamiento",
        description = "Consulta el resumen acumulado de los últimos 7 días desde el State Store de Kafka Streams"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Resumen encontrado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "resumen": {
                        "totalSesiones": 3,
                        "totalMinutos": 135,
                        "totalCalorias": 900,
                        "ultimaActualizacion": "2025-01-15T10:30:45"
                      }
                    }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "Sin datos para el miembro en los últimos 7 días",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "mensaje": "Sin datos para miembro: 1"
                    }
                """)
            )
        )
    })
    @GetMapping("/resumen/{miembroId}")
    public ResponseEntity<Map<String, Object>> obtenerResumen(
        @Parameter(description = "ID del miembro", example = "1", required = true)
        @PathVariable String miembroId
    ) {
        ReadOnlyWindowStore<String, ResumenEntrenamiento> store = streamsBuilderFactoryBean.getKafkaStreams().store(
            StoreQueryParameters.fromNameAndType(
                "resumen-entrenamiento-store",
                QueryableStoreTypes.windowStore()
            )
        );

        Instant ahora = Instant.now();
        Instant hace7dias = ahora.minus(7, ChronoUnit.DAYS);
        WindowStoreIterator<ResumenEntrenamiento> iterator = store.fetch(miembroId, hace7dias, ahora);

        Map<String, Object> response = new HashMap<>();
        if (iterator.hasNext()) {
            response.put("resumen", iterator.next().value);
        } else {
            response.put("mensaje", "Sin datos para miembro: " + miembroId);
        }
        iterator.close();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Iniciar proceso de recuperación",
        description = "Inicia un thread de recuperación que reanuda el procesamiento de mensajes Kafka desde el último checkpoint guardado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Proceso de recuperación iniciado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "mensaje": "Proceso de recuperacion iniciado"
                    }
                """)
            )
        )
    })
    @PostMapping("/recuperacion/iniciar")
    public ResponseEntity<Map<String, Object>> iniciarRecuperacion() {
        recuperacionService.iniciarProcesamientoAsync();
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proceso de recuperacion iniciado");
        return ResponseEntity.ok(response);
    }
}
