package de.hipp.pnp.base.fivee.converter.persistent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.persistence.AttributeConverter;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseConverter<Y> implements AttributeConverter<Y, String> {

  ObjectMapper objectMapper = new ObjectMapper();
  Logger logger = LoggerFactory.getLogger(this.getClass());

  Y type;

  @Override
  public String convertToDatabaseColumn(Y customerInfo) {

    String customerInfoJson = null;
    try {
      customerInfoJson = objectMapper.writeValueAsString(customerInfo);
    } catch (final JsonProcessingException e) {
      logger.error("JSON writing error", e);
    }

    return customerInfoJson;
  }

  @Override
  public Y convertToEntityAttribute(String customerInfoJSON) {

    Y valueInfo = null;
    if (Strings.isBlank(customerInfoJSON)) {
      return null;
    }
    try {
      valueInfo = objectMapper.readValue(
          customerInfoJSON,
          objectMapper.getTypeFactory().constructType(type.getClass())
      );
    } catch (final IOException e) {
      logger.error("JSON reading error", e);
    }

    return valueInfo;
  }

  public BaseConverter<Y> setType(Y type) {
    this.type = type;
    return this;
  }
}
