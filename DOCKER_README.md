# Docker Containerization - TopBloc Backend Challenge

## Overview

This directory contains Docker configuration to containerize the TopBloc Backend Challenge application using Docker and Docker Compose.

## Quick Start

### Prerequisites
- Docker installed and running
- Docker Compose installed

### Start the Application
```bash
# Option 1: Using helper script (recommended)
./docker.sh start

# Option 2: Using docker-compose directly
docker-compose up --build -d
```

### Access the Application
- **API**: http://localhost:4567
- **Database Browser**: http://localhost:8080 (optional SQLite web interface)

### Test the API
```bash
# Test version endpoint
curl http://localhost:4567/version

# Run comprehensive test suite
./docker.sh test
```

## Available Commands

### Using the Helper Script
```bash
./docker.sh start    # Build and start the application
./docker.sh stop     # Stop the application  
./docker.sh restart  # Restart the application
./docker.sh logs     # View application logs
./docker.sh test     # Run the test suite
./docker.sh reset    # Reset the database
./docker.sh status   # Show container status
./docker.sh clean    # Remove all Docker resources
```

### Using Docker Compose Directly
```bash
# Start services
docker-compose up --build -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f topbloc-api

# Run tests
docker-compose exec topbloc-api java -cp "target/classes" com.topbloc.codechallenge.TestSuite
```

## Services

### topbloc-api
- **Purpose**: Main API application
- **Port**: 4567
- **Health Check**: GET /version endpoint
- **Features**:
  - Multi-stage Docker build for optimization
  - Non-root user for security
  - Health checks
  - Volume mounting for database persistence

### sqlite-web (Optional)
- **Purpose**: Web-based SQLite database browser
- **Port**: 8080
- **Features**:
  - Browse database tables
  - Execute SQL queries
  - View data in web interface

## Architecture

```
┌─────────────────────┐    ┌─────────────────────┐
│   topbloc-api       │    │   sqlite-web        │
│   (Port 4567)       │    │   (Port 8080)       │
│                     │    │                     │
│ ┌─────────────────┐ │    │ ┌─────────────────┐ │
│ │   Java App      │ │    │ │ SQLite Browser  │ │
│ │   (Spark)       │ │    │ │                 │ │
│ └─────────────────┘ │    │ └─────────────────┘ │
│         │           │    │         │           │
└─────────┼───────────┘    └─────────┼───────────┘
          │                          │
          └──────────┬───────────────┘
                     │
              ┌─────────────┐
              │ challenge.db│
              │  (SQLite)   │
              └─────────────┘
```

## File Structure

```
.
├── Dockerfile              # Multi-stage build configuration
├── docker-compose.yml      # Service orchestration
├── .dockerignore           # Files to exclude from build
├── docker.sh              # Helper script for common operations
└── DOCKER_README.md        # This documentation
```

## Features

### Security
- Non-root user inside container
- Minimal base image (JRE slim)
- Health checks for monitoring

### Performance
- Multi-stage build (smaller final image)
- Layer caching optimization
- Dependency pre-download

### Development
- Volume mounting for database persistence
- Source code mounting for development
- Comprehensive helper scripts

### Monitoring
- Health checks on API endpoint
- Container status monitoring
- Log access

## Troubleshooting

### Container Won't Start
```bash
# Check container logs
./docker.sh logs

# Check container status
./docker.sh status
```

### Database Issues
```bash
# Reset database to clean state
./docker.sh reset

# Access database browser
open http://localhost:8080
```

### Port Conflicts
If ports 4567 or 8080 are in use, modify `docker-compose.yml`:
```yaml
ports:
  - "5000:4567"  # Change external port
```

### Performance Issues
```bash
# Check resource usage
docker stats

# Adjust memory limits in docker-compose.yml
environment:
  - JAVA_OPTS=-Xmx1g  # Increase memory
```

## Production Considerations

For production deployment:

1. **Remove SQLite Browser**:
   ```bash
   # Comment out sqlite-web service in docker-compose.yml
   ```

2. **Use External Database**:
   ```yaml
   # Add PostgreSQL or MySQL service
   ```

3. **Add Reverse Proxy**:
   ```yaml
   # Add Nginx or Traefik for SSL/load balancing
   ```

4. **Environment Variables**:
   ```yaml
   environment:
     - ENV=production
     - LOG_LEVEL=warn
   ```

5. **Resource Limits**:
   ```yaml
   deploy:
     resources:
       limits:
         memory: 512M
         cpus: '0.5'
   ```

This containerized setup provides a complete, production-ready environment for the TopBloc Backend Challenge application!
