

# Baraka Matching Engine.

- Order matching engine for only single asset FIFO Implementation
- Supports Partial order matching
- Implemented using Time/Price based Priority Queue.
- ConcurrentHashMap is used for In-Memory order storage.
- POST API: http://localhost:8080/orders
- GET API: http://localhost:8080/orders/{id}
- Junit Test coverage includes full order, partial order matching.
