package com.analisys.gimnasio.miembros_service.recovery;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.analisys.gimnasio.miembros_service.kafka.DatosEntrenamiento;

@Service
public class RecuperacionService {

    private static final Logger logger = LoggerFactory.getLogger(RecuperacionService.class);

    private final RecoveryConsumerFactory consumerFactory;
    private final CheckpointService checkpointService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public RecuperacionService(RecoveryConsumerFactory consumerFactory, CheckpointService checkpointService) {
        this.consumerFactory = consumerFactory;
        this.checkpointService = checkpointService;
    }

    public void iniciarProcesamientoAsync() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        Thread thread = new Thread(this::iniciarProcesamiento, "kafka-recovery");
        thread.setDaemon(true);
        thread.start();
    }

    public void detener() {
        running.set(false);
    }

    private void iniciarProcesamiento() {
        KafkaConsumer<String, DatosEntrenamiento> consumer = consumerFactory.createConsumer();

        try {
            consumer.subscribe(List.of("datos-entrenamiento"), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    // no-op
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    Map<TopicPartition, Long> ultimoOffsetProcesado = checkpointService.cargarUltimosOffsets();
                    for (TopicPartition partition : partitions) {
                        Long offset = ultimoOffsetProcesado.get(partition);
                        if (offset != null) {
                            consumer.seek(partition, offset + 1);
                            logger.info("Recuperacion: seek {}:{} -> {}", partition.topic(), partition.partition(), offset + 1);
                        }
                    }
                }
            });

            while (running.get()) {
             ConsumerRecords<String, DatosEntrenamiento> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, DatosEntrenamiento> record : records) {
                    procesarRecord(record);
                    checkpointService.guardarOffset(record.topic(), record.partition(), record.offset());
                }
            }
        } catch (Exception e) {
            logger.error("Error en recuperacion Kafka: {}", e.getMessage(), e);
        } finally {
            consumer.close();
            running.set(false);
        }
    }

    private void procesarRecord(ConsumerRecord<String, DatosEntrenamiento> record) {
        logger.info(
            "Reprocesando entrenamiento {} -> {}",
            record.key(),
            record.value()
        );
    }
}
