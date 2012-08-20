package com.wotifgroup.circuitbreaker;

public class GuardedService {

    private CircuitBreaker circuitBreaker;

    public void setCircuitBreaker(CircuitBreaker circuitBreakerSimple) {
        this.circuitBreaker = circuitBreakerSimple;
    }

    public CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public void execute(Command command) {
        if (!circuitBreaker.isCallable()) {
            throw new RuntimeException("Service Circuit Breaker in Open State");
        }

    }
}
