package org.zalando.nakadi.client.java.test.event.dce.payment;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.LoggerFactory;
import org.zalando.nakadi.client.java.enumerator.DataOperation;
import org.zalando.nakadi.client.java.model.DataChangeEvent;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventMetadata;
import org.zalando.nakadi.client.java.test.event.generator.EventGeneratorBuilder;

public class PaymentEventGenerator extends EventGeneratorBuilder {
    private final String id = RandomStringUtils.randomAlphanumeric(12);
    private int tokenNr = 3;
    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    protected Event getNewEvent() {
        Money money = new Money(120.00, "USD");
        PaymentCreated payment = null;
        try {
            payment = new PaymentCreated(//
                    randomNumeric(5), //
                    "Channel_1",//
                    money, //
                    new URI("https://zalando.test/token-" + randomNumeric(tokenNr) + "-" + randomNumeric(tokenNr)));
            String dataType = getEventTypeName();
            DataOperation dataOperation = DataOperation.CREATE;
            EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(),null,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()),
                    null, null, null, null);
            return new DataChangeEvent<PaymentCreated>(payment, dataType, dataOperation, metadata);
        } catch (URISyntaxException e) {
            log.error("An error occurred", e.getMessage());
            throw new RuntimeException("An error occurred",e);
        }

    }

    @Override
    protected String getEventTypeName() {
        return getEventTypeId() + id;
    }

    @Override
    protected String getSchemaDefinition() {
        return "{ 'properties': { 'order_number': { 'type': 'string' }, 'sales_channel_id': { 'type': 'string' }, 'payment_balance': { 'type': 'object', 'properties': { 'amount': { 'type': 'number' }, 'currency': { 'type': 'string' } } }, 'payment_token_reference': { 'type': 'string' } }}"
                .replaceAll("'", "\"");
    }

}
