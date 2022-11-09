package de.hipp.kafka.producer;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.List;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.producer.bootstrap-servers} ")
    String serverAdress;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                Map.of(BOOTSTRAP_SERVERS_CONFIG,
                        serverAdress,
                        RETRIES_CONFIG, 1,
                        BUFFER_MEMORY_CONFIG, 33554432,
                        KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                ));
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, List<String>> replyer(ProducerFactory<String, Object> pf,
        ConcurrentKafkaListenerContainerFactory<String, List<String>> containerFactory) {

        containerFactory.setReplyTemplate(kafkaTemplate(pf));
        ConcurrentMessageListenerContainer<String, List<String>> container = replyContainer(containerFactory);
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, List<String>> replyContainer(
        ConcurrentKafkaListenerContainerFactory<String, List<String>> containerFactory) {

        ConcurrentMessageListenerContainer<String, List<String>> container =
            containerFactory.createContainer("so63058608-2");
        container.getContainerProperties().setGroupId("so63058608-2");
        return container;
    }

    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<>(pf);
    }

}
