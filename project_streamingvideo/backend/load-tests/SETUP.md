# Complete Load Testing Setup Guide

## 🎯 What You Now Have

I've created a complete load testing suite for your Streaming API with:

- **3 k6 test scenarios** (load, stress, spike tests)
- **Artillery configuration** (alternative tool)
- **Performance monitor** (Python script)
- **Interactive test runner** (bash script)
- **Smoke test** (quick verification)

## 📁 Files Created

```
load-tests/
├── README.md                    # Detailed documentation
├── QUICKSTART.md               # Quick start guide (START HERE!)
├── k6-load-test.js            # Basic load test (10→100 users)
├── k6-stress-test.js          # Stress test (up to 400 users)
├── k6-spike-test.js           # Spike test (sudden bursts to 500)
├── k6-smoke-test.js           # Quick verification test
├── artillery-load-test.yml    # Artillery configuration
├── artillery-processor.js     # Artillery helper functions
├── monitor-performance.py     # Real-time performance monitor
└── run-load-test.sh          # Interactive test runner menu
```

## 🚀 Quick Start (3 Steps)

### Step 1: Install k6 (one-time setup)

```bash
brew install k6
```

Verify installation:
```bash
k6 version
```

### Step 2: Start Your API

In one terminal window:
```bash
cd /project_streamingvideo/backend
mvn spring-boot:run
```

Wait for: "Started StreamingApiApplication"

### Step 3: Run Your First Load Test

In another terminal window:
```bash
cd /project_streamingvideo/backend/load-tests

# Quick smoke test first (30 seconds)
k6 run k6-smoke-test.js

# If smoke test passes, run the full load test
k6 run k6-load-test.js
```

## 📊 Understanding the Results

After running a test, you'll see output like this:

```
     ✓ GET /movies status is 200
     ✓ GET /movies response time < 500ms

     checks.........................: 98.50% ✓ 1970    ✗ 30
     data_received..................: 2.4 MB 40 kB/s
     http_req_duration..............: avg=125ms min=45ms med=98ms max=850ms p(95)=285ms p(99)=450ms
     http_req_failed................: 0.50%  ✓ 10      ✗ 1990
     http_reqs......................: 2000   33.33/s
```

### What Each Metric Means:

| Metric | What It Means | Good Value |
|--------|---------------|------------|
| **checks** | % of successful validations | > 95% |
| **http_req_duration (p95)** | 95% of requests finish under this time | < 500ms |
| **http_req_failed** | % of failed requests | < 1% |
| **http_reqs** | Throughput (requests/second) | Depends on system |

### Performance Grading:

- ✅ **Excellent**: p95 < 100ms, errors < 0.1%
- ✅ **Good**: p95 < 300ms, errors < 1%
- ⚠️ **Fair**: p95 < 500ms, errors < 5%
- ❌ **Poor**: p95 > 500ms, errors > 5%

## 🎮 Interactive Test Runner

Use the menu-driven script for easy testing:

```bash
cd /project_streamingvideo/backend/load-tests
./run-load-test.sh
```

This will show you a menu:
```
1) Basic Load Test (k6)
2) Stress Test (k6)
3) Spike Test (k6)
4) Artillery Load Test
5) Quick Smoke Test
6) Run All Tests
7) Exit
```

## 🔍 Real-Time Monitoring

While your API is running, monitor it with Python:

```bash
python3 monitor-performance.py 60
```

This will:
- Test your endpoints every second for 60 seconds
- Show real-time statistics every 10 seconds
- Provide a performance assessment at the end

## 📈 Test Types Explained

### 1. Smoke Test (k6-smoke-test.js)
- **Duration**: 30 seconds
- **Users**: 5 concurrent
- **Purpose**: Verify API is working before heavy testing
- **When to use**: Before any load test

### 2. Load Test (k6-load-test.js)
- **Duration**: 6 minutes
- **Users**: Ramps 10 → 50 → 100
- **Purpose**: Test normal operating conditions
- **When to use**: Regular performance checks

### 3. Stress Test (k6-stress-test.js)
- **Duration**: 13 minutes
- **Users**: Ramps up to 400
- **Purpose**: Find the breaking point
- **When to use**: Capacity planning

### 4. Spike Test (k6-spike-test.js)
- **Duration**: 6 minutes
- **Users**: Sudden bursts to 500
- **Purpose**: Test system recovery from traffic spikes
- **When to use**: Before product launches or marketing campaigns

## 🛠️ Troubleshooting

### "Connection refused" Error
**Problem**: API is not running
**Solution**:
```bash
# Check if API is running
curl http://localhost:8080/api/movies

# Start the API
cd /project_streamingvideo/backend
mvn spring-boot:run
```

### High Error Rates (> 5%)
**Possible causes**:
- MongoDB connection issues
- Too many concurrent requests
- Database connection pool exhausted

**Solutions**:
1. Check MongoDB is running: `ps aux | grep mongod`
2. Reduce virtual users in test
3. Add connection pooling in application.properties

### Slow Response Times (p95 > 1s)
**Possible causes**:
- No database indexes
- Inefficient queries
- Limited resources

**Solutions**:
1. Add database indexes for frequently queried fields
2. Check for N+1 query problems
3. Monitor resource usage (CPU, memory)

## 💡 Best Practices

### Before Testing:
1. ✅ Test in a dedicated environment (not production!)
2. ✅ Ensure MongoDB has test data
3. ✅ Clear logs to avoid filling disk
4. ✅ Close unnecessary applications

### During Testing:
1. 📊 Monitor API logs in another terminal
2. 📊 Watch system resources (Activity Monitor / top)
3. 📊 Check MongoDB connection count
4. 📊 Note any error patterns

### After Testing:
1. 📝 Save test results
2. 📝 Document findings
3. 📝 Identify bottlenecks
4. 📝 Plan optimizations

## 🎯 Next Steps After First Test

1. **Analyze Results**: Identify slow endpoints
2. **Optimize**: Add indexes, caching, connection pooling
3. **Re-test**: Run tests again to measure improvements
4. **Scale**: Consider horizontal scaling if needed

## 📚 Additional Resources

- **k6 Documentation**: https://k6.io/docs/
- **Artillery Documentation**: https://www.artillery.io/docs
- **Performance Testing Guide**: https://k6.io/docs/test-types/introduction/

## 🆘 Need Help?

Check these files for more details:
- `QUICKSTART.md` - Step-by-step instructions
- `README.md` - Detailed documentation
- Individual test files have comments explaining each section

## Example Session

Here's what a complete test session looks like:

```bash
# Terminal 1: Start API
cd /project_streamingvideo/backend
mvn spring-boot:run
# Wait for "Started StreamingApiApplication"

# Terminal 2: Run tests
cd /project_streamingvideo/backend/load-tests

# Quick smoke test (30s)
k6 run k6-smoke-test.js

# Full load test (6 min)
k6 run k6-load-test.js

# Terminal 3 (optional): Monitor in real-time
cd /➜  load-tests git:(master) ✗ k6 run k6-load-test.js

         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6-load-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 100 max VUs, 7m0s max duration (incl. graceful stop):
              * default: Up to 100 looping VUs for 6m30s over 6 stages (gracefulRampDown: 30s, gracefulStop: 30s)

INFO[0000] Starting load test...                         source=console
INFO[0000] Base URL: http://localhost:8080/api           source=console
INFO[0396] Load test completed!                          source=console


  █ THRESHOLDS 

    errors
    ✓ 'rate<0.01' rate=0.00%

    http_req_duration
    ✓ 'p(95)<500' p(95)=98.08ms
    ✓ 'p(99)<1000' p(99)=117.43ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......: 23513   59.340022/s
    checks_succeeded...: 100.00% 23513 out of 23513
    checks_failed......: 0.00%   0 out of 23513

    ✓ GET /movies status is 200
    ✓ GET /movies response time < 500ms
    ✓ Search movies status is 200
    ✓ Get by genre status is 200
    ✓ Register status is 200 or 400
    ✓ GET /users status is 200
    ✓ POST /rankings completed

    CUSTOM
    errors.........................: 0.00%  0 out of 0

    HTTP
    http_req_duration..............: avg=24.77ms min=317µs med=1.84ms max=181.58ms p(90)=86.16ms p(95)=98.08ms
      { expected_response:true }...: avg=24.77ms min=317µs med=1.84ms max=181.58ms p(90)=86.16ms p(95)=98.08ms
    http_req_failed................: 0.00%  0 out of 20154
    http_reqs......................: 20154  50.862876/s

    EXECUTION
    iteration_duration.............: avg=7.15s   min=7.08s med=7.14s  max=7.31s    p(90)=7.2s    p(95)=7.22s  
    iterations.....................: 3359   8.477146/s
    vus............................: 1      min=1          max=100
    vus_max........................: 100    min=100        max=100

    NETWORK
    data_received..................: 2.1 GB 5.4 MB/s
    data_sent......................: 2.6 MB 6.5 kB/s




running (6m36.2s), 000/100 VUs, 3359 complete and 0 interrupted iterations
default ✓ [======================================] 000/100 VUs  6m30s
project_streamingvideo/backend/load-tests
python3 monitor-performance.py 60
```

---

**You're all set! Start with the smoke test and work your way up to load testing.** 🚀

