# Multi-stage Docker build for TopBloc Backend Challenge

# Stage 1: Build stage
FROM maven:3.8.6-openjdk-11-slim AS builder

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean compile -B

# Stage 2: Runtime stage
FROM maven:3.8.6-openjdk-11-slim

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create a non-root user for security and set up Maven directory
RUN groupadd -r topbloc && useradd -r -g topbloc topbloc
RUN mkdir -p /home/topbloc/.m2 && chown -R topbloc:topbloc /home/topbloc

# Copy the built application from builder stage
COPY --from=builder /app/target ./target
COPY --from=builder /app/pom.xml .
COPY --from=builder /app/src ./src

# Copy the database file
COPY challenge.db .

# Set ownership of all app files
RUN chown -R topbloc:topbloc /app

# Switch to non-root user
USER topbloc

# Expose the port that the application runs on
EXPOSE 4567

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:4567/version || exit 1

# Set the command to run the application with Maven (includes all dependencies)
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.topbloc.codechallenge.Main"]
