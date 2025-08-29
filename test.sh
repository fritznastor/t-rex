#!/bin/bash

# Simple Test Runner - Run this when the server is already running
echo "Running TopBloc Backend Code Challenge Test Suite"
echo "=================================================="
echo "Make sure your server is running on http://localhost:4567"
echo ""

# Compile and run the test suite
mvn compile exec:java -Dexec.mainClass="com.topbloc.codechallenge.TestSuite" -q
