# Final Report

## Foundational Concepts

### What is a distributed system?

- A coherent system consisting of a collection of independent computers (nodes)
- Scalability: more machines to handle increased load
- Fault Tolerance: system continues functioning even if some components fail

## Architectural pattern

### Circuit breaker

- Closed state (normal operation): requests pass through normally, failures are monitored
- Open state (preventing calls): after threshold of failures is reached, all requests immediately fail without attempting; **prevents overwhelmed services from additional load**
- Half-open state (testing recovery): after timeout period, allows limited test requests; if successful, circuit closes again; if still failing, circuit opens again

**Why use circuit breaker?**
- Fail fast: avoid hanging requests when a service is unavailable
- Recovery time: giving failing services time to recover
- Resilience: make systems more robust to downstream failures

### Client-server model

- Client: sends request to the server
- Server: accepts the requested process and delivers the data packets requested back to the client

### CQRS (Command Query Responsibility Separation)

## Communication & Coordination

### Communication protocols

**RPC (Remote Procedure Call)**

- When the Client starts up, it creates an exclusive callback queue.
- For an RPC request, the Client sends a message with two properties: reply_to, which is set to the callback queue and correlation_id, which is set to a unique value for every request.
- The request is sent to an rpc_queue queue.
- The RPC worker (aka: server) is waiting for requests on that queue. When a request appears, it does the job and sends a message with the result back to the Client, using the queue from the reply_to field.
- The client waits for data on the callback queue. When a message appears, it checks the correlation_id property. If it matches the value from the request it returns the response to the application.

**Message queueing**

RabbitMQ
- Producer, consumer
- queue size
- connection pool

## Consistency & Replication

### Data consistency Models

**ACID**

**Two-Phase Commit**

## Scalability & Performance

### Partitioning strategies

- Horizontal (scale out) vs. vertical scaling (scale up): more machines & increase resources of existing machines
- Sharding techniques (hash based, directory based, range based...): partition a database into parts

### Performance metrics & analysis

- Throughput, latency, availability
- Little's Law
- Bottleneck identification

## Theoretical Foundation

### CAP Theorem

- Consistency + Partition Tolerance vs. Availability + Partition Tolerance


