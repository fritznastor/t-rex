# Docker Full Stack Setup - TopBloc Backend Challenge

## Overview

This directory contains a complete Docker containerization setup for the TopBloc Backend Challenge application, including both the Java backend API and React frontend client.

## Quick Start

### Prerequisites
- Docker installed and running
- Docker Compose installed

### Start the Full Stack
```bash
# Using the helper script (recommended)
./docker.sh start

# Or using docker-compose directly
docker-compose up --build -d
```

### Access the Applications
- **Frontend (React App)**: http://localhost:3000
- **Backend API**: http://localhost:4567
- **Database Browser**: http://localhost:8080 (optional SQLite web interface)

### Test the Setup
```bash
# Test all endpoints
./docker.sh test

# Or test manually
curl http://localhost:4567/inventory
curl http://localhost:3000
```

## Available Commands

### Using the Helper Script
```bash
./docker.sh start     # Build and start all services
./docker.sh stop      # Stop all services
./docker.sh restart   # Restart all services
./docker.sh build     # Build all Docker images
./docker.sh logs      # View logs for all services
./docker.sh logs-api  # View backend API logs only
./docker.sh logs-web  # View frontend logs only
./docker.sh status    # Show container status and resource usage
./docker.sh test      # Run API endpoint tests
./docker.sh reset     # Reset the database to initial state
./docker.sh clean     # Remove all Docker resources
./docker.sh urls      # Show all service URLs
./docker.sh help      # Show help message
```

### Using Docker Compose Directly
```bash
# Start all services
docker-compose up --build -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f backend    # Backend logs
docker-compose logs -f frontend   # Frontend logs
docker-compose logs -f sqlite-web # Database browser logs

# Check status
docker-compose ps

# Restart a specific service
docker-compose restart frontend
```

## Services Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     TopBloc Full Stack                      │
├─────────────────────┬─────────────────────┬─────────────────┤
│   React Frontend    │   Java Backend      │  SQLite Browser │
│   (Port 3000)       │   (Port 4567)       │   (Port 8080)   │
│                     │                     │                 │
│ ┌─────────────────┐ │ ┌─────────────────┐ │ ┌─────────────┐ │
│ │   Nginx         │ │ │   Spark API     │ │ │ Web UI      │ │
│ │   + React SPA   │ │ │   + Maven       │ │ │ (Optional)  │ │
│ └─────────────────┘ │ └─────────────────┘ │ └─────────────┘ │
│         │           │         │           │         │       │
└─────────┼───────────┴─────────┼───────────┴─────────┼───────┘
          │                     │                     │
          └─────────────────────┬─────────────────────┘
                                │
                        ┌───────────────┐
                        │ challenge.db  │
                        │   (SQLite)    │
                        └───────────────┘
```

## Service Details

### Frontend (React + Nginx)
- **Container**: `topbloc-frontend`
- **Port**: 3000 (external) → 80 (internal)
- **Technology**: React 18 + TypeScript + Material-UI
- **Features**:
  - Complete inventory management interface
  - Distributor and item management
  - Real-time data with CRUD operations
  - Responsive design with modern UI
  - Production-optimized build served by Nginx

### Backend (Java Spark API)
- **Container**: `topbloc-backend`
- **Port**: 4567
- **Technology**: Java 11 + Spark Framework + Maven
- **Features**:
  - RESTful API with all CRUD endpoints
  - SQLite database integration
  - CORS-enabled for frontend communication
  - Health checks for monitoring
  - Multi-stage Docker build for optimization

### Database Browser (Optional)
- **Container**: `topbloc-db-browser`
- **Port**: 8080
- **Technology**: sqlite-web (Python-based)
- **Features**:
  - Web-based SQLite database browser
  - Execute SQL queries directly
  - View database schema and data
  - Useful for debugging and data inspection

## File Structure

```
.
├── Dockerfile                    # Backend multi-stage build
├── docker-compose.yml            # Service orchestration
├── docker.sh                     # Management script
├── DOCKER_README.md              # This documentation
├── frontend/
│   ├── Dockerfile                # Frontend multi-stage build
│   ├── nginx.conf                # Nginx configuration
│   ├── .dockerignore             # Frontend build exclusions
│   └── src/                      # React application source
├── src/                          # Java backend source
├── pom.xml                       # Maven configuration
└── challenge.db                  # SQLite database
```

## Environment Configuration

### Frontend Environment Variables
- `REACT_APP_API_URL`: Backend API URL (defaults to `http://localhost:4567`)

### Backend Environment Variables
- `JAVA_OPTS`: JVM options (defaults to `-Xmx512m`)

### Development vs Production
The setup works for both development and production:

**Development:**
- Volume mounting for live code changes
- Source maps enabled
- Development server features

**Production:**
- Optimized builds with minification
- Nginx serving static assets
- Health checks and monitoring
- Resource limits and security headers

## Networking

All services communicate through a custom Docker network (`topbloc-network`):
- Frontend can access backend via internal DNS name `backend:4567`
- Database is shared via volume mounting
- External access through mapped ports

## Volumes and Persistence

- **Database**: `./challenge.db` is mounted to persist data across restarts
- **Source Code**: Optionally mounted for development (backend only)

## Health Checks

All services include health checks:
- **Backend**: Tests `/inventory` endpoint
- **Frontend**: Tests nginx response
- **Database Browser**: Built-in health monitoring

## Troubleshooting

### Container Won't Start
```bash
# Check logs
./docker.sh logs
docker-compose logs [service-name]

# Check container status
./docker.sh status
```

### API Not Responding
```bash
# Test backend directly
curl http://localhost:4567/inventory

# Check backend logs
./docker.sh logs-api
```

### Frontend Not Loading
```bash
# Test frontend directly
curl http://localhost:3000

# Check frontend logs
./docker.sh logs-web

# Rebuild frontend if needed
docker-compose build frontend
```

### Database Issues
```bash
# Reset database
./docker.sh reset

# Access database browser
open http://localhost:8080
```

### Port Conflicts
If ports are already in use, modify `docker-compose.yml`:
```yaml
ports:
  - "3001:80"    # Frontend: Change 3000 to 3001
  - "4568:4567"  # Backend: Change 4567 to 4568
  - "8081:8080"  # DB Browser: Change 8080 to 8081
```

### Resource Issues
```bash
# Check resource usage
docker stats

# Adjust memory limits in docker-compose.yml
environment:
  - JAVA_OPTS=-Xmx1g  # Increase backend memory
```

## Production Considerations

For production deployment:

1. **Security Enhancements**:
   ```yaml
   # Add security headers, HTTPS, authentication
   ```

2. **External Database**:
   ```yaml
   # Replace SQLite with PostgreSQL/MySQL
   ```

3. **Load Balancing**:
   ```yaml
   # Add Nginx reverse proxy or Traefik
   ```

4. **Environment Secrets**:
   ```yaml
   # Use Docker secrets or external secret management
   ```

5. **Monitoring**:
   ```yaml
   # Add Prometheus, Grafana, or similar monitoring
   ```

6. **Backup Strategy**:
   ```bash
   # Implement database backup automation
   ```

## Development Workflow

1. **Start Development Stack**:
   ```bash
   ./docker.sh start
   ```

2. **Make Changes**:
   - Backend: Edit Java files, restart backend container
   - Frontend: Edit React files, rebuild frontend container

3. **Test Changes**:
   ```bash
   ./docker.sh test
   ```

4. **View Logs**:
   ```bash
   ./docker.sh logs
   ```

5. **Reset Database**:
   ```bash
   ./docker.sh reset
   ```

This Docker setup provides a complete, production-ready containerized environment for the TopBloc Backend Challenge application with both frontend and backend components!
