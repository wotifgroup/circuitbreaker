package com.wg.circuitbreaker;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class CircuitBreakerSimpleTest {

    private CircuitBreakerSimple cb;

    @Before
    public void onSetup() {
        cb = new CircuitBreakerSimple();
    }
    
    @Test
    public void testCircuitBreakerIsClosedOnInit() {
       assertEquals(cb.getState(), CircuitBreakerStatus.CLOSED);
    }

    @Test
    /**
     * To avoid the expense of synchronization, we
     * expect that in multithreaded environment we could get some recorded failures after circuitBreaker has been tripped.
     *
     */
    public void testShouldSilentlyIgnoreFailureRecordingIfCircuitBreakerIsOpen() {
        cb.tripBreaker();
        assertEquals(0, cb.getFailureCount());
        cb.recordFailure();
        assertEquals(0, cb.getFailureCount());
    }

    @Test
    public void testShouldTripBreakerWhenFailureThresholdExceeded() {
        cb.recordFailure();
        cb.recordFailure();
        cb.recordFailure();
        cb.recordFailure();
        cb.recordFailure();
        assertEquals(cb.getState(), CircuitBreakerStatus.CLOSED);
        cb.recordFailure();
        assertEquals(cb.getState(), CircuitBreakerStatus.OPEN);
    }

    @Test
    public void testShouldAttemptAResetWhenTimeoutOnOpenStateHasPassed() {
        cb.tripBreaker(5);
        assertEquals(cb.getState(), CircuitBreakerStatus.OPEN);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(cb.getState(), CircuitBreakerStatus.HALF_OPEN);

    }

    @Test
    public void testShouldTripBreakerWhenFailureCallMadeInHalfOpenState() {
        cb.setState(CircuitBreakerStatus.HALF_OPEN);
        cb.recordFailure();
        assertEquals(cb.getState(), CircuitBreakerStatus.OPEN);
        //todo - check timeout correctly applied
//        assertEquals(cb.getNextAttemptStart, CircuitBreakerStatus.java.OPEN);

    }

    @Test
    public void testShouldAttemptResetWhenSuccessfulCallMadeInHalfOpenState() {
        cb.setState(CircuitBreakerStatus.HALF_OPEN);
        cb.recordSuccess();
        assertEquals(cb.getState(), CircuitBreakerStatus.CLOSED);

    }


}
