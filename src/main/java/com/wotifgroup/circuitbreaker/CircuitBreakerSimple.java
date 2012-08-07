package com.wotifgroup.circuitbreaker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Simple Circuit Breaker based on the pattern in <a href="http://www.pragprog.com/titles/mnee">Release It!</a>
 * Which was also based on the Leaky Bucket Patter from PLOP 2.
 * <p/>
 * This class explicity has no synchronisation, run the exerciser to see how some recorded successes or failures get lost.
 * <p/>
 * <pre>
 * state {closed, open, half-open}
 * may have lower numbers - no difference between timeout and connection refused
 * <p/>
 * when Circuit is Closed:
 * on call = pass through
 * call succeeds = reset count
 * call fails = count failure
 * threshold reached = trip breaker. Open State
 * <p/>
 * when Circuit is Half-Open
 * on call = pass through
 * call succeeds = reset go. Close State
 * call fails = trip breaker. Open State
 * <p/>
 * when Circuit is Open
 * on call = return/fail
 * on timeout = attempt reset. Half-Open State
 * <pre>
 */
public class CircuitBreakerSimple implements CircuitBreaker {

    private int failureThreshold = 5;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private CircuitBreakerStatus state;
    private int timeout = 30000;
    private Logger LOG = LogManager.getLogger(CircuitBreakerSimple.class);

    /* if Open and this is in the future, next call after that time should allow a call */
    private long attemptResetAfter = 0;

    public CircuitBreakerSimple() {
        state = CircuitBreakerStatus.CLOSED;        
    }

    public CircuitBreakerSimple(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public CircuitBreakerStatus getState() {
        if (CircuitBreakerStatus.OPEN.equals(state) && System.currentTimeMillis() >= attemptResetAfter) {
            state = CircuitBreakerStatus.HALF_OPEN;
        }
        return state;
    }

    /**
     * record Failure
     * if Closed then failureCount++
     * if HalfOpen then go back to open
     *
     * @return
     */
    public int recordFailure() {
        if (isClosed() || isHalfOpen()) {
            failureCount.incrementAndGet();
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("failure count:" + failureCount);
        }

        if (isClosed() && (failureCount.intValue() > failureThreshold)) {
            tripBreaker();
            return failureCount.intValue();
        }

        if (isHalfOpen()) {
            tripBreaker();
        }

        return failureCount.intValue();
    }

    void setState(CircuitBreakerStatus state) {
        CircuitBreakerStatus priorState = this.state;

        if (this.state.equals(state)) {
            return;
        }

        this.state = state;
        String msg = "Circuit Breaker, State change from:%s => %s";
        LOG.info(String.format(msg, priorState, state));
    }

    public void reset() {
        setState(CircuitBreakerStatus.CLOSED);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Circuit Breaker reset");
        }
        failureCount.getAndSet(0);
    }

    public void tripBreaker() {
        tripBreaker(timeout);
    }

    public void tripBreaker(int timeout) {
        this.attemptResetAfter = System.currentTimeMillis() + timeout;
        if (LOG.isDebugEnabled()) {
            LOG.debug("trip breaker, allow Half-Open after: " + timeout);
        }
        setState(CircuitBreakerStatus.OPEN);
    }

    public int getFailureCount() {
        return failureCount.intValue();
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void recordSuccess() {
        if (!isClosed()) {
            reset();
            return;
        }

        if (isClosed() && failureCount.intValue() > 0) {
            failureCount.getAndSet(0);
            if (LOG.isDebugEnabled()) {
                LOG.debug("failure count reset. ");
            }
        }
    }

    public int recordFailure(String type) {
        return recordFailure();
    }


    public boolean isClosed() {
        return CircuitBreakerStatus.CLOSED.equals(state);
    }

    public boolean isHalfOpen() {
        return CircuitBreakerStatus.HALF_OPEN.equals(getState());
    }

    public boolean isOpen() {
        return CircuitBreakerStatus.OPEN.equals(state);
    }

    public boolean isCallable() {
        return isClosed() || isHalfOpen();
    }
}
