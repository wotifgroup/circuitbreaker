package com.wotifgroup.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static com.wotifgroup.circuitbreaker.CircuitBreakerStatus.*;
import static java.lang.String.format;
/**
 * Simple Circuit Breaker based on the pattern in <a href="http://www.pragprog.com/titles/mnee">Release It!</a>
 * Which was also based on the Leaky Bucket Patter from PLOP 2.
 * <p/>
 * This class explicitly has no synchronisation, run the exerciser to see how some recorded successes or failures get lost.
 * <p/>
 * <pre>
 * state {closed, open, half-open}
 * may have lower numbers - no difference between retryInterval and connection refused
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
 * on retryInterval = attempt reset. Half-Open State
 * <pre>
 */
public class CircuitBreakerSimple implements CircuitBreaker {

    private String name;
    private String title;
    private int failureThreshold = 5;
    private int retryInterval = 30000;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private CircuitBreakerStatus state;
    private long lastOpenTimestamp = Long.MAX_VALUE;
    /* if Open and this is in the future, next call after that time should allow a call */
    private long attemptResetAfter = 0;

    private Logger LOG = LoggerFactory.getLogger(CircuitBreakerSimple.class);

    public CircuitBreakerSimple() {
        name = "CircuitBreaker";
        state = CLOSED;
    }

    public CircuitBreakerSimple(String name, int failureThreshold, int timeout) {
        this();
        this.name = name;
        this.title = name;
        this.failureThreshold = failureThreshold;
        this.retryInterval = timeout;

    }

    public CircuitBreakerSimple(int failureThreshold) {
        this();
        this.failureThreshold = failureThreshold;
    }

    public CircuitBreakerStatus getState() {
        if (OPEN.equals(state) && System.currentTimeMillis() >= attemptResetAfter) {
            state = HALF_OPEN;
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
    public int onFailure() {
        if (isClosed() || isHalfOpen()) {
            failureCount.incrementAndGet();
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace(format("[%s] failure count: %d", name, failureCount.get()));
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
        LOG.info(format("[%s] %s => %s", name, priorState, state));
    }

    public void reset() {
        setState(CLOSED);
        if (LOG.isDebugEnabled()) {
            LOG.debug(format("[%s] reset", name));
        }
        failureCount.getAndSet(0);
    }

    public void tripBreaker() {
        tripBreaker(retryInterval);
    }

    public void tripBreaker(int timeout) {
        lastOpenTimestamp = System.currentTimeMillis();
        attemptResetAfter = lastOpenTimestamp + timeout;
        LOG.info(format("[%s] tripped, HALF-OPEN in %d", name, timeout));
        setState(OPEN);

    }

    public int getFailureCount() {
        return failureCount.intValue();
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void onSuccess() {
        if (!isClosed()) {
            reset();
            return;
        }

        if (isClosed() && failureCount.intValue() > 0) {
            failureCount.getAndSet(0);
            if (LOG.isDebugEnabled()) {
                LOG.debug(format("[%s] reset", name));
            }
        }
    }

    public int onFailure(String type) {
        return onFailure();
    }


    public boolean isClosed() {
        return CLOSED.equals(state);
    }

    public boolean isHalfOpen() {
        return HALF_OPEN.equals(getState());
    }

    public boolean isOpen() {
        return OPEN.equals(state);
    }

    public boolean isCallable() {
        return isClosed() || isHalfOpen();
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public long getLastOpenTimestamp() {
        return lastOpenTimestamp;
    }
}
