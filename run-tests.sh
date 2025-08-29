#!/bin/bash

# TopBloc Backend Code Challenge - Test Runner
# This script compiles and runs the comprehensive test suite

echo "Compiling test suite..."

# Compile the test suite
mvn compile -q

if [ $? -ne 0 ]; then
    echo "Compilation failed. Please fix compilation errors first."
    exit 1
fi

echo "Compilation successful!"
echo ""

# Check if server is running
echo "Checking if server is running..."
if ! curl -s "http://localhost:4567/version" > /dev/null 2>&1; then
    echo "Server is not running. Starting server in background..."
    echo "Starting server..."
    mvn exec:java -Dexec.mainClass="com.topbloc.codechallenge.Main" -q &
    SERVER_PID=$!
    echo "Server started with PID: $SERVER_PID"
    
    # Wait for server to be ready
    echo "Waiting for server to be ready..."
    for i in {1..30}; do
        if curl -s "http://localhost:4567/version" > /dev/null 2>&1; then
            echo "Server is ready!"
            break
        fi
        if [ $i -eq 30 ]; then
            echo "Server failed to start within 30 seconds"
            kill $SERVER_PID 2>/dev/null
            exit 1
        fi
        sleep 1
    done
    
    STARTED_SERVER=true
else
    echo "Server is already running!"
    STARTED_SERVER=false
fi

echo ""
echo "Running comprehensive test suite..."
echo ""

# Run the test suite
mvn exec:java -Dexec.mainClass="com.topbloc.codechallenge.TestSuite" -q

TEST_EXIT_CODE=$?

# Clean up if we started the server
if [ "$STARTED_SERVER" = true ]; then
    echo ""
    echo "Stopping server..."
    kill $SERVER_PID 2>/dev/null
    wait $SERVER_PID 2>/dev/null
    echo "Server stopped."
fi

echo ""
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "Test suite completed successfully!"
else
    echo "Test suite completed with issues. Check output above for details."
fi

exit $TEST_EXIT_CODE
