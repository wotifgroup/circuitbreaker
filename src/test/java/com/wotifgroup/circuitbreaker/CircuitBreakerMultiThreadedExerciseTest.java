package com.wotifgroup.circuitbreaker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class CircuitBreakerMultiThreadedExerciseTest {
        private CircuitBreakerSimple cb;
        private Random rnd = new Random();
        private static final Logger LOG = LogManager.getLogger(CircuitBreakerSimple.class);


        @Before
        public void onSetup() {
            cb = new CircuitBreakerSimple();
            cb.setTimeout(10);
        }


        @Rule
        public ContiPerfRule i = new ContiPerfRule();

        public CircuitBreakerMultiThreadedExerciseTest() {
            super();    //To change body of overridden methods use File | Settings | File Templates.
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
                LOG.info("success");
                cb.recordSuccess();
            }
            else {
                cb.recordFailure();
            }
        }

}
