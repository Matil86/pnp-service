package de.hipp.kafka;

import de.hipp.pnp.E5EGameTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class TopicFactory {
    private TopicFactory(){
        for (int i = 0; i < E5EGameTypes.values().length; i++) {
            NewTopic topic = this.getNewTopic(E5EGameTypes.values()[i].name());
            log.info("Topic {} created",topic);
        }
    }
    private final HashMap<String, NewTopic> cache = new HashMap<>();

    public NewTopic getNewTopic(String name){
        cache.computeIfAbsent(name, key -> TopicBuilder.name(name)
                .partitions(10)
                .replicas(1)
                .build());
        return cache.get(name);
    }
}
