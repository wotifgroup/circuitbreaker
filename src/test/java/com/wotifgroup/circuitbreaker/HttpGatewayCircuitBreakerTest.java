package com.wotifgroup.circuitbreaker;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.junit.Assert.assertTrue;

/**
 * HttpGatewayCircuitBreakerTest
 * Any calls through a HttpGateway will return Http Response codes to match availability of upstream server.
 * This is just a demo, ignore the command interfaces, as how it http clients fit in with the gateway is tbd.
 * 503 - Service Unavailable = CircuitBreaker Open
 * 504 - Gateway Timeout = timed-out
 * 502 - Bad Gateway
 * todo: optionally expand the read timeout errors into MS specific ones
 * 598 Network read timeout error (Unknown) used by Microsoft Corp. HTTP proxies to signal a network read timeout behind the proxy to a client in front of the proxy.
 * 599 Network connect timeout error (Unknown)used by Microsoft Corp. HTTP proxies to signal a network connect timeout behind the proxy to a client in front of the proxy.
 */

public class HttpGatewayCircuitBreakerTest {
    private HttpGateway gw;

    public HttpGatewayCircuitBreakerTest() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Before
    public void setup() {
        gw = new HttpGateway();
        gw.setCircuitBreaker(new CircuitBreakerSimple());
    }

    @Test
    public void openCircuitBreakerResultIs503() {
        gw.getCircuitBreaker().tripBreaker();
        assertTrue(HttpURLConnection.HTTP_UNAVAILABLE == gw.execute(new Command()));
    }

    @Test
    public void closedCircuitBreakerResultIs200() {
        assertTrue(HttpURLConnection.HTTP_OK == gw.execute(new Command()));
    }

    //  what states should CB automatically fail on, all 5xx
//  401, 403 for unchanged cached client details?
//  These interactions are for another abstraction
    @Ignore
    public void originServerResponseOf500IsPassedThruAndUpdatesCircuitBreakerFailureCount() {
        assertTrue(HttpURLConnection.HTTP_INTERNAL_ERROR == gw.execute(new Command()));
    }

    @Test
    public void timedoutCallResultIs504() {
        ExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                try {
                    Thread.sleep(10);
                    return new Thread(runnable);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    return null;
                }

            }
        });
        IHttpGateway timed_gw = new GatewayBuilder().with(executor).build();
        //todo: where is the fixed with delay scheduler?
        assertTrue(HttpURLConnection.HTTP_GATEWAY_TIMEOUT == timed_gw.execute(new Command()));
    }
}
