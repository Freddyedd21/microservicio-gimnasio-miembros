package com.analisys.gimnasio.miembros_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    public static final String EXCHANGE_GIMNASIO = "gimnasio.exchange";
    public static final String QUEUE_MIEMBRO_EVENTO = "miembro.evento.queue";
    public static final String BINDING_KEY_MIEMBRO_EVENTO = "miembro.evento.#";

    public static final String QUEUE_MIEMBROS_HORARIO = "miembros.horario.queue";
    public static final String BINDING_KEY_HORARIO = "clase.horario.#";

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_GIMNASIO);
    }

    @Bean
    @SuppressWarnings("removal")
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    @Bean
    public Queue miembroEventoQueue() {
        return new Queue(QUEUE_MIEMBRO_EVENTO, true);
    }   

    @Bean
    public Binding miembroEventoBinding(Queue miembroEventoQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(miembroEventoQueue).to(topicExchange).with(BINDING_KEY_MIEMBRO_EVENTO);
    }

    @Bean
    public Queue miembrosHorarioQueue() {
        return new Queue(QUEUE_MIEMBROS_HORARIO, true);
    }

    @Bean
    public Binding miembrosHorarioBinding(Queue miembrosHorarioQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(miembrosHorarioQueue).to(topicExchange).with(BINDING_KEY_HORARIO);
    }

    
}
