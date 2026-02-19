# Stop guessing. Start testing.: Hibernate-Queries im Griff

This repository contains the demo code for the JUG Bonn talk **"Stop guessing. Start testing.: Hibernate-Queries im Griff"**.

It shows how to make Hibernate and JPA behavior visible in integration tests by capturing and asserting the generated SQL, so that common persistence and performance problems can be detected early instead of being discovered through log inspection or in production.

## What this demo covers

Among others, the examples cover:

- query capturing in integration tests
- `LazyInitializationException`
- the pitfalls of `open-in-view`
- the N+1 query problem
- fetch strategies such as `JOIN FETCH`, `@EntityGraph`, and `@BatchSize`
- in-memory pagination caused by collection fetches
- redundant `save()` calls and unnecessary merges

## Prerequisites

To run the demo locally, you need:

- JDK 25
- Docker running locally (required by Testcontainers)

## Running the demo

Start the test suite:

```bash
./gradlew test
```

The integration tests start PostgreSQL via Testcontainers and compare the captured SQL with the validation files in [`data/test/validation`](data/test/validation).

## Project structure

- [`src/main/java/de/cronn/hibernatetalk/Application.java`](src/main/java/de/cronn/hibernatetalk/Application.java): demo application and Hibernate/JPA examples
- [`src/test/java/de/cronn/hibernatetalk/IntegrationTest.java`](src/test/java/de/cronn/hibernatetalk/IntegrationTest.java): end-to-end tests for the demo scenarios
- [`src/test/java/de/cronn/hibernatetalk/test/QueryValidationTraits.java`](src/test/java/de/cronn/hibernatetalk/test/QueryValidationTraits.java): query capture and assertion helpers
- [`data/test/validation`](data/test/validation): expected SQL snapshots used by the tests

## Reconstructing the talk

The progression of the talk can be followed through the commit history. Each commit corresponds to a step in the live demo:

1. `Initial commit`
2. `Capturing`
3. `N+1`
4. `@BatchSize`
5. `Join fetch`
6. `EntityGraph`
7. `Open in view`
8. `Redundant save`
9. `Hibernate dirty checking`
10. `In-Memory Pagination`
11. `Fail on in-memory pagination and @BatchSize`

A convenient way to walk through the demo is: 
```bash 
git log --oneline --reverse
```

