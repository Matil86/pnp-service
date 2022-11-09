package de.hipp.pnp.api.fivee;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DefaultMessage<T> {

  private String action;
  private T payload;
  private String detailMessage;
  private String uuid;

}
