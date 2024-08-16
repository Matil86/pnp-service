package de.hipp.pnp.boundry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.rabbitmq.DataProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocaleRestController {
    final DataProducer dataProducer;
    final ObjectMapper mapper;

    public LocaleRestController(DataProducer dataProducer, ObjectMapper mapper) {
        this.dataProducer = dataProducer;
        this.mapper = mapper;
    }

    @GetMapping("/locale")
    public String getLocale(@RequestParam(value = "gameType", defaultValue = "1") int gameType) throws JsonProcessingException {
        var locale = dataProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US");
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(locale);
    }
}
