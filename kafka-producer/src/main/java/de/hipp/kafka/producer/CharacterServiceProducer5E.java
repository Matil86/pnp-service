package de.hipp.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.E5EGameTypes;
import de.hipp.pnp.interfaces.I5ECharacter;
import de.hipp.pnp.interfaces.I5ECharacterClass;
import de.hipp.pnp.interfaces.I5ECharacterRace;
import java.util.Set;
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
    ObjectMapper mapper = new ObjectMapper();

    public CharacterServiceProducer5E(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public I5ECharacter generate(int gameType) throws JsonProcessingException {
        String uuid = UUID.randomUUID().toString();
        E5EGameTypes gameTypes = E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK) ;
        I5ECharacter character = new I5ECharacter() {
            @Override
            public int getGameType() {
                return 0;
            }

            @Override
            public String getFirstName() {
                return null;
            }

            @Override
            public String getLastName() {
                return null;
            }

            @Override
            public Integer getLevel() {
                return null;
            }

            @Override
            public I5ECharacterRace getRace() {
                return null;
            }

            @Override
            public Set<? extends I5ECharacterClass> getCharacterClasses() {
                return null;
            }
        };
        ListenableFuture<SendResult<String, Object>> result = this.template.send(gameTypes.name() + "_generate", mapper.writeValueAsString(character));
        result.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                ProducerRecord<String, Object> producerRecord = result.getProducerRecord();
                if (producerRecord != null) {
                    Object data = producerRecord.value();
                    cache.put(uuid, data);
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
