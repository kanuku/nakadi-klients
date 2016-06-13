package org.zalando.nakadi.client.java.test.factory.events;

import org.zalando.nakadi.client.java.model.Event;

public class MySimpleEvent implements Event {

	private String orderNumber;

	public MySimpleEvent(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

}
