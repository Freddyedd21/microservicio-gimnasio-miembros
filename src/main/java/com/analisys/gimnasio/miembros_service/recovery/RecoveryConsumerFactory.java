package com.analisys.gimnasio.miembros_service.recovery;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import com.analisys.gimnasio.miembros_service.kafka.DatosEntrenamiento;

@Component
public class RecoveryConsumerFactory {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public KafkaConsumer<String, DatosEntrenamiento> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "recovery-grupo");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.analisys.gimnasio.miembros_service.kafka");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<DatosEntrenamiento> valueDeserializer = new JsonDeserializer<>(DatosEntrenamiento.class);
        valueDeserializer.addTrustedPackages("com.analisys.gimnasio.miembros_service.kafka");

        return new KafkaConsumer<>(props, new StringDeserializer(), valueDeserializer);
    }
}
