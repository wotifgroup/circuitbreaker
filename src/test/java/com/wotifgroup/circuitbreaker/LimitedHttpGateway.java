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

