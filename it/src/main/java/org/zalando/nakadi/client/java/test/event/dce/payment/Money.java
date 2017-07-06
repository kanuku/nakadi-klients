package org.zalando.nakadi.client.java.test.event.dce.payment;

import com.fasterxml.jackson.annotation.*;

public class Money {

    private double amount;
    private String currency;

    @JsonCreator
    public Money(@JsonProperty("amount") double amount, //
            @JsonProperty("currency") String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

}
