# Overview
Based on Michael Nygard's work from Release It!

# Interesting about CB
- Ensure distributed communications are time bound so you can avoid Cascading Failures
- Policies determine the failure state handling, the testing during half-open state
- Failure handling if OPEN is managed by the callee

# Monitoring and alerting annoyances
- static guesses of thresholds, in a complex world
- accumulative affect of thresholds
- dealing with false positives of true negatives
- how do you gather the information to adjust the hard thresholds
- half-open policies that don't have a stance (aggressive, wait_for_a_human...)
- don't fill logs
- allow tuning of samples (not just on or off 100%) and sample types to reduce the overhead
- esper, or an esper-like evaluation
- publish state, or publish gauge of state change, to zookeeper
- report outage times

# Things a circuit breaker should be tolerant of
- flapping, one bad service, with a failing health-monitor

# Design
- use of internal events, sdisruptor or eventbus
- how async or block with pools affects the design
- policies that are functions
- probabilistic circuit breaker, bloom style
- streams, not yet
- event bus source => fetch => fail
- either the event bus or ListenableFutures
