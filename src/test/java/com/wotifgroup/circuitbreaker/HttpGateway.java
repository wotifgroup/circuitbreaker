package com.wotifgroup.circuitbreaker;

import com.google.common.util.concurrent.RateLimiter;

import java.net.HttpURLConnection;

public class HttpGateway implements IHttpGateway {

    private CircuitBreaker circuitBreaker;

    public HttpGateway() {
        circuitBreaker = new CircuitBreakerSimple();
    }

    public HttpGateway(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public int execute(ICommand command) {
        if (!circuitBreaker.isCallable()) {
            return HttpURLConnection.HTTP_UNAVAILABLE;
        }
        return HttpURLConnection.HTTP_OK;
    }

    public void setCircuitBreaker(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public int execute(ICommand command, RateLimiter rateLimiter) {
        return HttpURLConnection.HTTP_BAD_REQUEST;
    }

}
