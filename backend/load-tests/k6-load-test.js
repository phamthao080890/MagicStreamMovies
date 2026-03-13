import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp up to 10 users over 30s
    { duration: '1m', target: 50 },   // Ramp up to 50 users over 1 minute
    { duration: '2m', target: 50 },   // Stay at 50 users for 2 minutes
    { duration: '30s', target: 100 }, // Ramp up to 100 users
    { duration: '2m', target: 100 },  // Stay at 100 users for 2 minutes
    { duration: '30s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    'http_req_duration': ['p(95)<500', 'p(99)<1000'], // 95% of requests should be below 500ms
    'errors': ['rate<0.01'], // Error rate should be less than 1%
    'http_req_failed': ['rate<0.01'],
  },
};

// Base URL - Change this to your API URL
const BASE_URL = 'http://localhost:8080/api';

// Test data
const testUsers = [
  { email: `loadtest${__VU}@test.com`, password: 'TestPass123', name: `User${__VU}` }
];

export function setup() {
  console.log('Starting load test...');
  console.log('Base URL:', BASE_URL);
}

export default function () {
  const userId = `u${__VU}_${__ITER}`;

  // Test 1: Get all movies
  let res = http.get(`${BASE_URL}/movies`);
  check(res, {
    'GET /movies status is 200': (r) => r.status === 200,
    'GET /movies response time < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);

  sleep(1);

  // Test 2: Search movies
  res = http.get(`${BASE_URL}/movies/search?q=action`);
  check(res, {
    'Search movies status is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(1);

  // Test 3: Get movies by genre
  res = http.get(`${BASE_URL}/movies/genre/Action`);
  check(res, {
    'Get by genre status is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(1);

  // Test 4: Register user
  const registerPayload = JSON.stringify({
    name: `LoadTestUser${__VU}_${__ITER}`,
    email: `user${__VU}_${__ITER}@loadtest.com`,
    password: 'TestPassword123'
  });

  res = http.post(`${BASE_URL}/auth/register`, registerPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, {
    'Register status is 200 or 400': (r) => r.status === 200 || r.status === 400,
  }) || errorRate.add(1);

  sleep(1);

  // Test 5: Get all users
  res = http.get(`${BASE_URL}/users`);
  check(res, {
    'GET /users status is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(1);

  // Test 6: Get rankings
  res = http.post(`${BASE_URL}/rankings`, JSON.stringify({
    ranking_value: "5",
    rangking_name: "Excellent"
  }), {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, {
    'POST /rankings completed': (r) => r.status >= 200 && r.status < 500,
  }) || errorRate.add(1);

  sleep(2); // Think time between iterations
}

export function teardown(data) {
  console.log('Load test completed!');
}

