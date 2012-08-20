package com.wotifgroup.circuitbreaker;

public interface IHttpGateway {
    int execute(ICommand command);
    CircuitBreaker getCircuitBreaker();
    void setCircuitBreaker(CircuitBreaker circuitBreaker);
}
