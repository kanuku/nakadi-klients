package de.zalando.nakadi.client;


import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

// TODO should we make it immutable?
public class ClientBuilder {

    private OAuth2TokenProvider tokenProvider;
    private URI endpoint;

    public ClientBuilder() {
    }

    public ClientBuilder withEndpoint(final URI endpoint) {
        checkState(this.endpoint == null, "endpoint is already set");
        this.endpoint = checkNotNull(endpoint, "endpoint must not be null");

        return this;
    }

    public ClientBuilder withOAuth2TokenProvider(OAuth2TokenProvider provider) {
        checkState(tokenProvider == null, "OAut2TokenProvider is already set");
        tokenProvider = checkNotNull(provider, "OAuth2TokenProvider must not be null");

        return this;
    }

    public Client build() {
        checkState(tokenProvider != null, "no OAuth2 token provider set -> try withOAuth2TokenProvider(myProvider)");
        checkState(endpoint != null, "endpoint is set -> try withEndpoint(new URI(\"http://localhost:8080\"))");

        return new NakadiClientImpl(endpoint, tokenProvider);
    }

}
