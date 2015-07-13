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


