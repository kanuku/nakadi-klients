package org.zalando.nakadi.client.java.test.event.dce.payment;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentCreated {

    private String orderNumber;

    private String salesChannelId;

    private Money paymentBalance;

    private URI paymentTokenReference;

    public PaymentCreated(
            @JsonProperty("order_number") String orderNumber, //
            @JsonProperty("sales_channel_id") String salesChannelId, //
            @JsonProperty("payment_balance") Money paymentBalance, //
            @JsonProperty("payment_token_reference") URI paymentTokenReference) {
        this.orderNumber = orderNumber;
        this.salesChannelId = salesChannelId;
        this.paymentBalance = paymentBalance;
        this.paymentTokenReference = paymentTokenReference;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSalesChannelId() {
        return salesChannelId;
    }

    public Money getPaymentBalance() {
        return paymentBalance;
    }

    public URI getPaymentTokenReference() {
        return paymentTokenReference;
    }

}