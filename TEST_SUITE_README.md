# Test Suite - Quick Start Guide

## What It Does
Runs 79 comprehensive tests on all API endpoints to make sure everything works correctly.

## How to Run

### 1. Start Your Server
```bash
mvn exec:java -Dexec.mainClass="com.topbloc.codechallenge.Main"
```

### 2. Run Tests (in another terminal)
```bash
mvn compile
java -cp "target/classes" com.topbloc.codechallenge.TestSuite
```

That's it! 

## What You'll See

**Green checkmarks** = Tests passed  
**Red X marks** = Tests failed  

Example output:
```
TOPBLOC BACKEND CODE CHALLENGE - COMPREHENSIVE TEST SUITE
======================================================================

BASIC ENDPOINTS
--------------------------------------------------
PASS GET /version
PASS GET /items
PASS GET /reset

INVENTORY ENDPOINTS  
--------------------------------------------------
PASS GET /inventory - Valid
PASS GET /inventory/out-of-stock - Valid
...

======================================================================
TEST RESULTS SUMMARY
======================================================================
Total Tests: 79
Passed: 79
Failed: 0
Success Rate: 100.0%

ALL TESTS PASSED! Your API is working perfectly!
======================================================================
```

## What Gets Tested

- **All endpoints**: GET, POST, PUT, DELETE
- **Valid data**: Normal requests that should work
- **Invalid data**: Bad requests that should fail gracefully  
- **Edge cases**: Empty values, very long strings, special characters
- **Security**: SQL injection attempts, XSS attacks

## When to Run

Run this test suite:
- Before committing code changes
- After adding new features
- When debugging issues  
- To make sure everything still works

## Troubleshooting

**Tests failing?**
1. Make sure your server is running on `http://localhost:4567`
2. Try resetting the database: `curl "http://localhost:4567/reset"`
3. Check the test output for specific error messages

**Can't compile?**
1. Run `mvn clean compile` first
2. Make sure you have Java 11+ installed

That's all you need to know! Keep your API quality high by running tests regularly.
