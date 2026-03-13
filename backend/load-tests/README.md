# Load Testing Scripts for Streaming API

This directory contains load testing scripts to test the performance and capacity of the Streaming API.

## Tools Included

1. **k6** - Modern, developer-friendly load testing tool
2. **Apache JMeter** - Traditional GUI-based load testing tool
3. **Artillery** - Node.js based load testing

## Prerequisites

### Install k6 (Recommended)
```bash
# macOS
brew install k6

# Or download from https://k6.io/docs/getting-started/installation/
```

### Install Artillery (Alternative)
```bash
npm install -g artillery
```

### Install JMeter (GUI Option)
```bash
brew install jmeter
```

## Running Load Tests

### Option 1: k6 (Recommended)

Basic load test:
```bash
k6 run k6-load-test.js
```

Stress test:
```bash
k6 run k6-stress-test.js
```

Spike test:
```bash
k6 run k6-spike-test.js
```

### Option 2: Artillery

```bash
artillery run artillery-load-test.yml
```

### Option 3: JMeter

```bash
# GUI mode (for creating/editing tests)
jmeter -t jmeter-streaming-api.jmx

# CLI mode (for running tests)
jmeter -n -t jmeter-streaming-api.jmx -l results.jtl -e -o report
```

## Test Scenarios

### 1. Load Test
- Gradual ramp-up of users
- Tests normal operating conditions
- Identifies baseline performance

### 2. Stress Test
- Pushes system beyond normal capacity
- Identifies breaking point
- Tests recovery capabilities

### 3. Spike Test
- Sudden bursts of traffic
- Tests system elasticity
- Simulates viral events

## Metrics to Monitor

- **Response Time**: Average, p95, p99
- **Throughput**: Requests per second
- **Error Rate**: Percentage of failed requests
- **Resource Usage**: CPU, Memory, Database connections

## Before Running Tests

1. **Start the API server**:
   ```bash
   cd /project_streamingvideo/backend
   mvn spring-boot:run
   ```

2. **Ensure MongoDB is running**:
   ```bash
   # Check if MongoDB is running
   ps aux | grep mongod
   ```

3. **Configure test parameters** in the script files:
   - Base URL
   - Number of virtual users
   - Test duration
   - Ramp-up time

## Interpreting Results

### Good Performance
- Response time < 200ms for 95% of requests
- Error rate < 0.1%
- Consistent throughput under load

### Warning Signs
- Response time > 500ms
- Error rate > 1%
- Increasing response times over duration
- Memory leaks or CPU spikes

## Reports

Results will be saved in:
- k6: Console output + optional HTML reports
- Artillery: HTML reports in the same directory
- JMeter: `report/` directory with HTML dashboard

