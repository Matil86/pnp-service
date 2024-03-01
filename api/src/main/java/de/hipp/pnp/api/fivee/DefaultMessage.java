package de.hipp.pnp.api.fivee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultMessage<T> {

    private String action;
    private T payload;
    private String detailMessage;
    private String uuid;

}
