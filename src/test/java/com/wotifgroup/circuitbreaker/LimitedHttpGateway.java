package com.wotifgroup.circuitbreaker;

import com.google.common.util.concurrent.UncheckedTimeoutException;
import sun.net.www.protocol.http.HttpURLConnection;

public class LimitedHttpGateway implements IHttpGateway {
    private IHttpGateway gw;

    public LimitedHttpGateway(IHttpGateway gw) {
        this.gw = gw;
    }

    public int execute(ICommand command) {
        try {
            return gw.execute(command);
        } catch (UncheckedTimeoutException timeoutException) {
            return HttpURLConnection.HTTP_GATEWAY_TIMEOUT;
        }
    }

    public CircuitBreaker getCircuitBreaker() {
        return gw.getCircuitBreaker();
    }

    public void setCircuitBreaker(CircuitBreaker circuitBreaker) {
        gw.setCircuitBreaker(circuitBreaker);
    }

}

