# CS6650 Distributed Systems: Midterm Report

## Team Project

### Scalable Distributed Music Service on AWS

**Technologies:** Golang, Java, AWS (EC2, RDS, ALB), Terraform, LocalStack, RabbitMQ, Postman, Swagger

**Techniques:** Load Balancing, Circuit Breaker Pattern, Asynchronous Processing, Multithreading

- Designed and implemented a scalable distributed music service that allows user to store and retrieve album information, including artist name, album title, and cover image.
- Developed a RESTful API in Java and Golang that saves album metadata in AWS RDS (**MySQL**) and stores cover image in **Amazon S3**.
- Refactored backend from Java to Golang, leveraging **Goroutines** for concurrent request handling, increasing throughput by **18.5%**.
- Automated AWS infrastructure provisioning using **Terraform**, deploying AWS Load Balancer and EC2 instance, reducing average CPU usage by **44%** (from 90% to 50%) and increasing request success rate by **62.5%** (from 40% to 65%).
- Developed a high-performace load-testing client in Java, simulating concurrent requests with **multithreading**, measuring latency metrics (mean, median, P99 response times) for performance evaluation.
- Implemented **RabbitMQ** for asynchronous processing, decoupling API request handling from background tasks, improving scalability and resilience under high load.
- Applied the **Circuit Breaker** pattern to prevent cascading failures when RabbitMQ was unavailable, ensuring system availability and preventing unnecessary retries that could degrade performance.

## Key Concepts

### CAP Theorem

In a distributed system, we can only achieve at most two out of the three properties (trade-off):
- Consistency: All nodes see the same data at the same time.
- Availability: Every request gets a response (no guaranteed latest data).
- Partition Tolerance: The system continues working despite network failures.

Here's a comparison of DynamoDB and MongoDB. By default, they're AP and CP. However, their configurations can be changed (but at the core it's still the CAP trade-off).
|      Feature    |DynamoDB (AP by default)|MongoDB (CP by default)|
|-----------------|------------------------|-----------------------|
|CAP Tradeoff|AP (Availability + Partition Tolerance)|CP (Consistency + Partition Tolerance)|
|Consistency Model|Eventual consistency|Strong consistency|
|Availability|Always available, even if some nodes are partitioned|Unavailable for writes during primary node failover|
|Partition Tolerance|Tolerates network failures and keeps serving requests|Tolerates partitions but prioritizes consistency|
|Write Behavior|Writes go through even if some nodes are unreachable|Writes are rejected if consistency cannot be guaranteed|
|Read Behavior|Reads may return outdated data|Reads always return the latest committed data|
|Primary Failover Handling|No strict primary node, writes go to multiple regions| If the primary fails, writes pause until a new primary is elected|

### Transactions (2PC & 3PC)

Transaction: ACID properties

Atomicity: The entire transaction takes place at once or doesn't happen at all.

Consistency: A transaction must bring the database from one valid state to another, following all integrity rules and constraints.

Isolation: Transactions do not interfere with each other, preventing race conditions.

Durability: Once committed, a transaction’s changes are permanent, even in case of crashes.

**2PC (Two-Phase Commit)**: prepare phase -> commit phase

Blocking issue: If the coordinator crashes after the Prepare Phase, participants wait indefinitely because they don’t know whether to commit or abort.

**3PC (Three-Phase Commit)**: prepare phase -> pre-commit phase -> commit phase

3PC introduces a timeout mechanism to prevent indefinite blocking but adds extra overhead.

### Hashtable vs. HashMap vs. ConcurrentHashMap

|Feature|Hashtable|HashMap|ConcurrentHashMap|
|-------|---------|-------|-----------------|
|Thread Safety|Yes (Fully synchronized)|No|Yes (Partially synchronized)|
|Performance|Slow due to full synchronization|Fast (No synchronization overhead)|Faster than Hashtable, uses fine-grained locking|
|Concurrency Mechanism| Locks entire table| No locks|Uses bucket-level locking|
|Null Keys/Values Allowed?|No null keys/values| One null key, multiple null values|One null key, multiple null values|

For bucket-level locking: Each bucket has its own lock, so multiple threads can write to different buckets concurrently.

### Load Balancing

Types:

|Type|	Description	|Example Use Case|
|----|--------------|----------------|
|DNS Load Balancing|	Distributes traffic by returning different IP addresses based on geolocation or round-robin DNS. |Global website traffic distribution|
|Network Load Balancing (Transport Level)|	Balances traffic based on TCP/UDP connections without looking at HTTP headers.|	AWS Elastic Load Balancer|
|Application Load Balancing (Application Level)	|Balances traffic based on HTTP headers, cookies, or request path.	|API gateways, routing requests to microservices|

Algorithms:
|Algorithm|	How It Works|	Best For|
|---------|-------------|---------|
|Round Robin	|Distributes requests sequentially across all servers.	|Evenly distributing traffic across servers, less overhead|
|Least Connections	|Routes traffic to the server with the fewest active connections.	|Handling long-lived requests like database queries.|
|IP Hashing|	Assigns requests from the same IP to the same server.|	Maintaining session affinity (e.g., shopping carts).|
|Weighted Round Robin	|Assigns more traffic to higher-capacity servers.|	Heterogeneous server environments.|


## Project Plan

I plan to dig deeper into the current milestone project, especially in optimizing the database performance. Possible directions:

1. Implement Read Replicas in AWS RDS for high-traffic handling.
2. Introduce Redis Cache for frequently accessed album metadata, reducing DB calls.

Moreover, it will be worthy of time to explore the containerization and CI/CD integration.
