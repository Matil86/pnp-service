package de.hipp.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import de.hipp.pnp.api.fivee.DefaultMessage;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.fivee.interfaces.I5ECharacter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CharacterServiceProducer5E {

  private final KafkaTemplate<String, Object> template;
  private final HashMap<String, Object> cache = new HashMap<>();
  ObjectMapper mapper = new ObjectMapper();

  public CharacterServiceProducer5E(KafkaTemplate<String, Object> template) {
    this.template = template;
  }

  public String generate(int gameType) {
    String uuid = UUID.randomUUID().toString();
    E5EGameTypes gameTypes = E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK);
    var message = new DefaultMessage<I5ECharacter>();
    message.setAction("generate");
    message.setUuid(uuid);
    this.template.send(gameTypes.name() + "_generate", message);
    return uuid;
  }

  @KafkaListener(id = "pnp", topics = "generate_finished")
  public void populateCache(String message) throws JsonProcessingException {
    log.info("-----------------------------------------");
    log.info(message);
    log.info("-----------------------------------------");
    DefaultMessage<JSONPObject> mappedMessage = mapper.readValue(message, new TypeReference<>() {});
    if (Objects.nonNull(mappedMessage) && Objects.nonNull(mappedMessage.getUuid())
        && Objects.nonNull(mappedMessage.getPayload())) {
      cache.put(mappedMessage.getUuid(), mappedMessage.getPayload());
    }
  }

  public List<Object> getAllCharacters() {
    return List.copyOf(cache.values());
  }
}
