package com.wg.circuitbreaker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
    private int failureCount = 0;
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
     * if HalfOpen then go to open
     * if
     *
     * @return
     */
    public int recordFailure() {
        if (isClosed() || isHalfOpen()) {
            failureCount++;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("failure count:" + failureCount);
        }

        if (isClosed() && (failureCount > failureThreshold)) {
            tripBreaker();
            return failureCount;
        }

        if (isHalfOpen()) {
            tripBreaker();
        }

        return failureCount;
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
        failureCount = 0;
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
        return failureCount;
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

        if (isClosed() && failureCount > 0) {
            failureCount = 0;
            if (LOG.isDebugEnabled()) {
                LOG.debug("failure count reset to: " + failureCount);
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
