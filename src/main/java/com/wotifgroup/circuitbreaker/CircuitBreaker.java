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

public interface CircuitBreaker {

    boolean isCallable();

    void tripBreaker(int timeout);

    void tripBreaker();

    void reset();

    boolean isClosed();

    boolean isOpen();

    boolean isHalfOpen();

    void onSuccess();

    int onFailure();

    int onFailure(String type);

    long getLastOpenTimestamp = 0;

}
