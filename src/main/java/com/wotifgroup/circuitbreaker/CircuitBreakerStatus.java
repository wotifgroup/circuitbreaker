package com.wg.circuitbreaker;
public enum CircuitBreakerStatus {
    CLOSED,
    OPEN,
    HALF_OPEN,
    OPEN_FOREVER,
    CLOSED_FOREVER
}
