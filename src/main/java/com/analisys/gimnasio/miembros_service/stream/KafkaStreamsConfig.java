package com.analisys.gimnasio.miembros_service.stream;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.analisys.gimnasio.miembros_service.kafka.DatosEntrenamiento;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.application-id:miembros-entrenamiento-streams}")
    private String applicationId;

    @Bean(name = org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KafkaStreams kafkaStreams(StreamsBuilderFactoryBean streamsBuilderFactoryBean) throws Exception {
        streamsBuilderFactoryBean.setAutoStartup(true);
        streamsBuilderFactoryBean.getObject();
        return streamsBuilderFactoryBean.getKafkaStreams();

    }

    @Bean
    public KStream<String, DatosEntrenamiento> kStream(StreamsBuilder streamsBuilder) {
        JsonSerde<DatosEntrenamiento> datosSerde = new JsonSerde<>(DatosEntrenamiento.class);
        JsonSerde<ResumenEntrenamiento> resumenSerde = new JsonSerde<>(ResumenEntrenamiento.class);

        KStream<String, DatosEntrenamiento> stream = streamsBuilder.stream(
            "datos-entrenamiento",
            Consumed.with(Serdes.String(), datosSerde)
        );

        stream.groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(7)))
            .aggregate(
                ResumenEntrenamiento::new,
                (key, value, aggregate) -> aggregate.actualizar(value),
                Materialized.<String, ResumenEntrenamiento, WindowStore<Bytes, byte[]>>as("resumen-entrenamiento-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(resumenSerde)
            )
            .toStream()
            .map((windowedKey, value) -> KeyValue.pair(windowedKey.key(), value))
            .to("resumen-entrenamiento", Produced.with(Serdes.String(), resumenSerde));

        return stream;
    }
}