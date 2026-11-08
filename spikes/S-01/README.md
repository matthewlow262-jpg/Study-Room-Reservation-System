# S-01 — Concurrent Reservation Conflict Prevention

## Architectural question
Can a transactional SQLite-backed reservation operation prevent overlapping bookings when 20 simulated students concurrently request the same room and time slot?

## Requirements and scope
- FR-09: prevent overlapping reservations for the same room.
- QR-02: 20 concurrent attempts; exactly one success and 19 rejections; repeat 100 times.
- UC-02: Reserve a Study Room.

This is an isolated proof of concept, **not** a deployed application or performance/load test of a web API. It tests a proposed SQLite transaction strategy, not the team's finalized production architecture.

## Setup and run
Requires Python 3.9+; no third-party dependencies.

From this directory:

```powershell
python reservation_spike.py --trials 100 --students 20 --output results.csv
```

Each trial creates a fresh SQLite database, launches 20 threads with separate database connections, and checks the stored reservation count. `BEGIN IMMEDIATE` serializes the check-and-insert critical section. The overlap predicate detects intersecting time intervals, not only identical starts.

## Pass criteria
Every trial must report exactly one success, 19 conflict rejections, zero SQLite errors, and exactly one stored reservation. The script exits with code 1 if any trial fails.

## Evidence
Run the script locally, commit the generated `results.csv`, and record its printed summary, Python version, operating system, and commit hash in the P2 Section 8 write-up. Do not claim a successful result until the experiment has actually been run.

## Limitations
Threads run in a single Python process, SQLite has one writer at a time, and this test does not exercise distributed services, actual authentication, network latency, or deployment-scale throughput. A later implementation must preserve atomic conflict checking in its chosen database.
