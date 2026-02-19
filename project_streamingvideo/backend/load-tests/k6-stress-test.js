import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Stress Test Configuration - Push system to its limits
export const options = {
  stages: [
    { duration: '1m', target: 50 },    // Warm up
    { duration: '2m', target: 100 },   // Increase load
    { duration: '3m', target: 200 },   // Stress level
    { duration: '3m', target: 300 },   // Beyond normal capacity
    { duration: '2m', target: 400 },   // Breaking point
    { duration: '2m', target: 0 },     // Ramp down and recover
  ],
  thresholds: {
    'http_req_duration': ['p(95)<1000', 'p(99)<2000'],
    'errors': ['rate<0.05'], // Allow up to 5% errors in stress test
    'http_req_failed': ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // High-intensity API calls
  const endpoints = [
    `${BASE_URL}/movies`,
    `${BASE_URL}/movies/search?q=test`,
    `${BASE_URL}/users`,
    `${BASE_URL}/movies/genre/Action`,
  ];

  // Randomly select endpoint
  const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];

  const res = http.get(endpoint);
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time OK': (r) => r.timings.duration < 2000,
  }) || errorRate.add(1);

  sleep(0.5); // Shorter sleep time for stress test
}

export function teardown(data) {
  console.log('Stress test completed!');
  console.log('Check the results to identify the breaking point.');
}

