package com.wotifgroup.circuitbreaker.httpclient;

import com.yammer.dropwizard.client.HttpClientConfiguration;

public class HttpClientFactory extends com.yammer.dropwizard.client.HttpClientFactory {

//    todo: wait the instrumented timing manager doesn't count error class by default
    public HttpClientFactory(HttpClientConfiguration config) {
        super(config);
    }


}
