package de.hipp.pnp.api.fivee;

import org.springframework.stereotype.Component;

@Component
public class DefaultMessage<T> {

  private String action;
  private T payload;
  private String detailMessage;

  public String getAction() {
    return action;
  }

  public DefaultMessage<T> setAction(String action) {
    this.action = action;
    return this;
  }

  public T getPayload() {
    return payload;
  }

  public DefaultMessage<T> setPayload(T payload) {
    this.payload = payload;
    return this;
  }

  public String getDetailMessage() {
    return detailMessage;
  }

  public DefaultMessage<T> setDetailMessage(String detailMessage) {
    this.detailMessage = detailMessage;
    return this;
  }
}
