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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class CircuitBreakerMultiThreadedExerciseTest {
        private CircuitBreakerSimple cb;
        private Random rnd = new Random();

        @Before
        public void onSetup() {
            cb = new CircuitBreakerSimple();
            cb.setRetryInterval(10);
        }

        @Rule
        public ContiPerfRule i = new ContiPerfRule();

        public CircuitBreakerMultiThreadedExerciseTest() {
            super();
        }

        /**
         * Let's have a test that shows a bit of flip flopping between states with a slight preference to failure.
         */
        @Test
        @PerfTest(invocations = 100, threads = 20)
//        @Required(max = 1200, average = 250)
        public void testCircuitBreakerIsClosedOnInit() throws InterruptedException {
            int nextInt = rnd.nextInt(10);
            if (nextInt < 3) {
                cb.onSuccess();
            } else {
                cb.onFailure();
            }
        }

}
