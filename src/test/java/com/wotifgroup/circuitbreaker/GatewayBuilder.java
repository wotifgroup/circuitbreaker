package com.wotifgroup.circuitbreaker;

import com.google.common.util.concurrent.SimpleTimeLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class GatewayBuilder {

    private SimpleTimeLimiter limiter = new SimpleTimeLimiter();
    private IHttpGateway gw;

    public GatewayBuilder() {
        this.gw = new HttpGateway();
    }

    public GatewayBuilder(IHttpGateway gw) {
        this.gw = gw;
    }

    public GatewayBuilder with(ExecutorService es) {
        limiter = new SimpleTimeLimiter(es);
        return this;
    }

    public IHttpGateway build() {
        IHttpGateway timed_gw = limiter.newProxy(gw, IHttpGateway.class, 1, TimeUnit.NANOSECONDS);
        return new LimitedHttpGateway(timed_gw);
    }

}


