package com.wg.circuitbreaker;

public interface CircuitBreaker {

    boolean isCallable();

    void tripBreaker(int timeout);

    void tripBreaker();

    void reset();

    boolean isClosed();

    boolean isOpen();

    boolean isHalfOpen();

    void recordSuccess();

    int recordFailure();

    int recordFailure(String type);

}
