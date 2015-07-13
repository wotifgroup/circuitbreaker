/*
 * #%L
 * circuitbreaker
 * %%
 * Copyright (C) 2012 - 2015 Wotif Group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.wotifgroup.circuitbreaker;
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
        cb.onFailure();
        assertEquals(0, cb.getFailureCount());
    }

    @Test
    public void testShouldTripBreakerWhenFailureThresholdExceeded() {
        cb.onFailure();
        cb.onFailure();
        cb.onFailure();
        cb.onFailure();
        cb.onFailure();
        assertEquals(cb.getState(), CircuitBreakerStatus.CLOSED);
        cb.onFailure();
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
        cb.onFailure();
        assertEquals(cb.getState(), CircuitBreakerStatus.OPEN);
        //todo - check timeout correctly applied
//        assertEquals(cb.getNextAttemptStart, CircuitBreakerStatus.java.OPEN);

    }

    @Test
    public void testShouldAttemptResetWhenSuccessfulCallMadeInHalfOpenState() {
        cb.setState(CircuitBreakerStatus.HALF_OPEN);
        cb.onSuccess();
        assertEquals(cb.getState(), CircuitBreakerStatus.CLOSED);

    }


}
