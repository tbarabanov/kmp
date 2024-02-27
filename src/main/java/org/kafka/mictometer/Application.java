package org.kafka.mictometer;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@EnableKafka
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @KafkaListener(topics = "pike")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println(record.topic());
    }

    @Bean
    public DefaultKafkaConsumerFactoryCustomizer kafkaConsumerFactoryCustomizer(MeterRegistry registry) {
        return consumerFactory -> consumerFactory.addListener(new MicrometerConsumerListener<>(registry));
    }

    @Component
    public static class ConcurrentKafkaListenerContainerFactoryCustomizer {
        public ConcurrentKafkaListenerContainerFactoryCustomizer(ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory) {
            kafkaListenerContainerFactory.setContainerCustomizer(container -> container.getContainerProperties().setMicrometerTagsProvider(consumerRecord -> {
                var random = ThreadLocalRandom.current();
                var tags = new HashMap<String, String>();
                tags.put("value.status", random.nextBoolean() ? "success" : "failure");
                if (random.nextBoolean()) tags.put("value.a", "A");
                else tags.put("value.b", "B");
                return tags;
            }));
        }
    }

}
