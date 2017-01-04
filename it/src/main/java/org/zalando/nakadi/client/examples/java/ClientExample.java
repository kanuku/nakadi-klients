package org.zalando.nakadi.client.examples.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.scala.ClientFactory;

public class ClientExample {

	private static <T> Optional<List<T>> unwrap(
			Future<Optional<List<T>>> result)
			throws InterruptedException, ExecutionException {
		return result.get();
	}

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		final Client client = ClientFactory.buildJavaClient();

		Future<Optional<List<EventType>>> result = client
				.getEventTypes();

		Optional<List<EventType>> opt = ClientExample.unwrap(result);

		opt.ifPresent(new Consumer<List<EventType>>() {
			@Override
			public void accept(List<EventType> t) {
				System.out.println(">>>>" + t);
			}
		});
		while (true) {

		}
	}

}
