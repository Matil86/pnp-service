package de.hipp.pnp.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public abstract class Receiver {

	protected Receiver() {
		log.info(this.getClass().getName() + " created");
	}

	private final CountDownLatch latch = new CountDownLatch(1);

	public String receiveMessage(String message) {
		log.info("Received <" + message + ">");
		String response = "";
		try {
			response = handleMessage(message);
		} catch (JsonProcessingException e) {
			log.error("Something went wrong during message handling:", e);
		}
		latch.countDown();
		return response;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	protected abstract String handleMessage(String message) throws JsonProcessingException;

}
