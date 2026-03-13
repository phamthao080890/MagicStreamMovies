# Quick Start Guide - Load Testing the Streaming API

## Prerequisites

Install k6 (recommended and easiest):
```bash
brew install k6
```

Or install Artillery:
```bash
npm install -g artillery
```

## Step 1: Start Your API

In a new terminal:
```bash
cd /project_streamingvideo/backend
mvn spring-boot:run
```

Wait for the message: "Started StreamingApiApplication"

## Step 2: Run Load Tests

### Option A: Interactive Menu (Easiest)
```bash
cd /project_streamingvideo/backend/load-tests
./run-load-test.sh
```

### Option B: Run Individual Tests

**Basic Load Test** (Recommended for first run):
```bash
cd /project_streamingvideo/backend/load-tests
k6 run k6-load-test.js
```

**Stress Test** (Find breaking point):
```bash
k6 run k6-stress-test.js
```

**Spike Test** (Sudden traffic bursts):
```bash
k6 run k6-spike-test.js
```

## Step 3: Understanding Results

### k6 Output Explanation:

```
checks.........................: 95.00%  ✓ 1900      ✗ 100
data_received..................: 2.4 MB  40 kB/s
data_sent......................: 156 kB  2.6 kB/s
http_req_blocked...............: avg=1.2ms    min=1µs    med=3µs    max=200ms   p(95)=5ms
http_req_duration..............: avg=150ms    min=50ms   med=100ms  max=2s      p(95)=400ms
http_reqs......................: 2000    33.33/s
```

**Key Metrics:**
- ✅ **checks**: Should be > 95% (percentage of successful validation checks)
- ✅ **http_req_duration p(95)**: Should be < 500ms (95% of requests complete in this time)
- ✅ **http_reqs**: Throughput in requests per second
- ❌ **http_req_failed**: Should be < 1%

### What's Good Performance?

| Metric | Excellent | Good | Fair | Poor |
|--------|-----------|------|------|------|
| Response Time (p95) | < 100ms | < 300ms | < 500ms | > 500ms |
| Error Rate | < 0.1% | < 1% | < 5% | > 5% |
| Throughput | > 1000/s | > 500/s | > 100/s | < 100/s |

## Step 4: Monitor Your System

While tests are running, monitor in another terminal:

**Check API logs:**
```bash
# Watch the Spring Boot console output
```

**Monitor system resources:**
```bash
# CPU and Memory
top

# Or use Activity Monitor (GUI)
```

**Check MongoDB:**
```bash
# Connect to MongoDB and check connections
mongo
> db.serverStatus().connections
```

## Common Issues & Solutions

### Issue: "Connection refused"
**Solution**: Make sure the API is running on port 8080
```bash
curl http://localhost:8080/api/movies
```

### Issue: High error rate
**Possible causes:**
- Database connection pool exhausted
- API not handling concurrent requests well
- MongoDB slow queries
**Solution**: Check API logs and reduce virtual users

### Issue: Slow response times
**Possible causes:**
- No database indexes
- N+1 query problems
- Insufficient resources
**Solution**: Add database indexes, optimize queries

## Test Scenarios Included

1. **Load Test**: Gradual increase 10 → 50 → 100 users
2. **Stress Test**: Push to 400 users to find limits
3. **Spike Test**: Sudden bursts to 500 users

## Next Steps

After running tests:

1. **Optimize slow endpoints** identified in results
2. **Add database indexes** for frequently queried fields
3. **Configure connection pools** based on load
4. **Add caching** for frequently accessed data (Redis)
5. **Scale horizontally** if needed (multiple instances)

## Example: Optimizing Based on Results

If `/api/movies` is slow:
```java
// Add index in MongoDB
@Indexed
private String title;

// Add caching
@Cacheable("movies")
public List<Movie> findAll() {
    return movieRepository.findAll();
}
```

## Save and Compare Results

```bash
# Save results with timestamp
k6 run --out json=results-$(date +%Y%m%d-%H%M%S).json k6-load-test.js

# Generate HTML report (requires k6-reporter)
k6 run --out json=report.json k6-load-test.js
docker run --rm -v $(pwd):/k6 -w /k6 loadimpact/k6-reporter report.json
```

## Questions?

Check the main README.md for detailed information about each test type and configuration options.

