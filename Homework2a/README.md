# Homework 2a: Threads

## 1. Multithreaded

- takes a time stamp, and starts N threads
- each thread increments a shared synchronized counter 10 times, and then terminates
- when all threads are completed, the main thread takes a time stamp and prints out the counter value and the duration it took to run the program

**Observation:**

- **Race Condition**: Without *synchronized* in thread method header, multiple threads access a shared resource at the same time. Some updates are lost because threads overwrite each other's work.
- As threads increase, time per increment goes up.
  - Amdahl’s Law
  - maximum speedup is limited by the serial portion of the task

## 2. Single Thread - Vector & Array List

**Vector**: synchronized, extra overhead (locking/unlocking), slower

**Array List**: not synchronized, not safe in multithreaded environment

## 3. Single Thread - HashMap & HashTable

**HashMap**: not synchronized, not thread safe

**HashTable**: synchronized, safe in multithreaded environment

## 4. File Access

### Write every string to the file immediately after it is generated in the loop in each thread

slowest; frequent I/O operations

### Write all the strings from one thread after they are generated and just before a thread terminates

fast

### Store all the strings from all threads in a shared collection, and write this to a file from your main() thread after all threads are completed

fast; BlockingQueue (thread-safe, synchronization) / Priority Queue (sort elements in timestamp)


|Feature|One Thread Writes While Others Generate|Store Everything & Write After Completion|
---|---|---
|Memory Usage|Low (since queue processes data incrementally)|High (stores all strings in memory before writing)|
|File Writing Order|Unordered unless using a PriorityQueue|Can be sorted before writing|
|Performance|Better for large-scale data|Can be slow for large data due to sorting|
|Use Case|Real-time logging, continuous processing|Batch processing when writing order is important|
