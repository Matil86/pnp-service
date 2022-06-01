package de.hipp.kafka.producer;

import de.hipp.pnp.E5EGameTypes;
import de.hipp.pnp.interfaces.I5ECharacter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class CharacterServiceProducer5E {

    private final KafkaTemplate<String,Object> template;
    private final HashMap<String, Object> cache = new HashMap<>();

    public CharacterServiceProducer5E(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public I5ECharacter generate(int gameType){
        String uuid = UUID.randomUUID().toString();
        E5EGameTypes gameTypes = E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK) ;
        ListenableFuture<SendResult<String, Object>> result = this.template.send(gameTypes.name(), "generateCharacter");
        result.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                ProducerRecord<String, Object> producerRecord = result.getProducerRecord();
                if (producerRecord != null) {
                    Object data = producerRecord.value();
                    cache.put(uuid,data);
                    log.info(String.valueOf(data));
                }
            }

            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                throw ex;
            }
        });
        return null;
    }

    public List<Object> getAllCharacters() {
        return List.copyOf(cache.values());
    }
}
