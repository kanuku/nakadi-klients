package org.zalando.nakadi.client.examples.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.model.PartitionStrategy;
import org.zalando.nakadi.client.utils.ClientBuilder;
import org.zalando.nakadi.client.scala.ClientFactory;

public class ClientExample {
	private static final String token = ClientFactory.getToken();

	private static Optional<List<PartitionStrategy>> unwrap(
			Future<Optional<List<PartitionStrategy>>> result)
			throws InterruptedException, ExecutionException {
		return result.get();
	}

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		final Client client = new ClientBuilder()//
				.withHost("nakadi-sandbox.aruha-test.zalan.do")//
				.withSecuredConnection(true) // s
				.withVerifiedSslCertificate(false) // s
				.withTokenProvider4Java(() -> token)//
				.buildJavaClient();

		Future<Optional<List<PartitionStrategy>>> result = client
				.getPartitioningStrategies();

		Optional<List<PartitionStrategy>> opt = ClientExample.unwrap(result);

		opt.ifPresent(new Consumer<List<PartitionStrategy>>() {
			@Override
			public void accept(List<PartitionStrategy> t) {
				System.out.println(">>>>" + t);
			}
		});
		while (true) {

		}
	}

}
