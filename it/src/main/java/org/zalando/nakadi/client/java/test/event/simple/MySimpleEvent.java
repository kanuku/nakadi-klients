package org.zalando.nakadi.client.java.test.event.simple;

import org.zalando.nakadi.client.java.model.Event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MySimpleEvent implements Event {

	
	private String orderNumber;
	@JsonCreator
	public MySimpleEvent(@JsonProperty("order_number") String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

}
