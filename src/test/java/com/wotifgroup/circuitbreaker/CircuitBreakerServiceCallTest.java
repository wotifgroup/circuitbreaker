package com.wotifgroup.circuitbreaker;

import org.junit.Before;
import org.junit.Test;

/**
 * Service call test against various implementation support
 * - synchronous block
 * - call to service with thread pools at max capacity
 * - async call with future timeout
 */

public class CircuitBreakerServiceCallTest {
    private GuardedService gs;

    @Before
    public void setup() {
        gs = new GuardedService();
        gs.setCircuitBreaker(new CircuitBreakerSimple());
    }

    @Test(expected = RuntimeException.class)
    public void synchronousCallToOpenCBReturns() {
        gs.getCircuitBreaker().tripBreaker();
        gs.execute(new Command());
    }


}
