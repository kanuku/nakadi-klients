package org.zalando.nakadi.client.examples.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.model.Cursor;

public class EventCounterListener implements
		org.zalando.nakadi.client.java.Listener<MeetingsEvent> {
	private final String id;
	private AtomicLong eventCounter = new AtomicLong(0);
	private AtomicLong calledCounter = new AtomicLong(0);
	private Map<Long, MeetingsEvent> maps = new HashMap<Long, MeetingsEvent>();
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public EventCounterListener(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void onReceive(String eventUrl, Cursor cursor,
			List<MeetingsEvent> events) {
		calledCounter.addAndGet(1);
		for (MeetingsEvent event : events) {
			Integer startIndex = event.getTopic().indexOf('n');
			Long number = Long.valueOf(event.getTopic().substring(
					startIndex + 1));

			if (maps.get(number) != null) {
				log.error("Event {} [ALREADY EXISTS] {}", event,
						maps.get(number));
				System.exit(0);
			}

			maps.put(number, event);
			if (eventCounter.get() == (number - 1)) {
				eventCounter.addAndGet(1);
			} else {
				log.info("Current:" + eventCounter.get() + " Received "
						+ number);
			}
		}

		log.info("#####################################");
		log.info("Received " + events.size());
		log.info(String.format("Has a total of %d events", eventCounter.get()));
		log.info(String.format("Was called  %d time", calledCounter.get()));
		log.info("#####################################");

	}

	@Override
	public void onError(String eventUrl, java.util.Optional<ClientError> error) {
		if (error.isPresent()) {
			ClientError clientError = error.get();
			log.error("An error occurred" + clientError.getMsg());
		}

	}

	public void onSubscribed(String endpoint, Optional<Cursor> cursor) {
		log.info("########## onSubscribed ############");
	    log.info("Endpoint " + endpoint );
	    log.info("Cursor " + cursor );
		log.info("#####################################");
	}
}