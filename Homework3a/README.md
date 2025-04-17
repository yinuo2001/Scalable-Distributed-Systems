# Homework 3: Little's Law

1. Little's Law: L (concurrency) = λW (throughput * latency)

Concurrency: number of requests being processed at the same time
Throughput: requests completed per second
Latency: time taken per request

2. Thread limits:

Thread-per-request model causes OS to struggle scheduling threads efficiently
Latency increases, which reduces request throughput

3. Strategies to improve scalability:

**Reduce latency**

Parallel requests (async calls) – instead of calling services sequentially, send requests simultaneously
Timeouts and circuit breakers – prevent the system from hanging on low services
Increase server capacity

**Callback in Node.js (prevent using threads)**

Promises/Futures, even better than callbacks (no callback hells)

Lightweight threads – user-level, not OS threads
