# Overview
Based on Michael Nygard's work from Release It!

# Interesting about CB
- Ensure distributed communications are time bound
- Policies determine the failure state handling, the testing during half-open state
- Failure hanlding if OPEN is managed by the callee

# Things I always hated
- static guesses of thresholds, in a complex world
- accumulative affect of thresholds
- information to adjust the hard thresholds
- half-open policies that don't have a stance (aggressive, wait_for_a_human...)
- don't fill logs
- do some sampling
- flapping, one bad service, with a failing health-monitor
- esper, or an esper-lite evaluation
- publish state, or publish gauge of state change
- report outage times
- publish states to zookeeper

# Design
- use of internal events, sdisruptor or eventbus
- how async or block with pools affects the design
- policies that are functions
- probablistic circuit breaker, bloom style
- streams, not yet
- event bus source => fetch => fail
- either the event bus or ListenableFutures