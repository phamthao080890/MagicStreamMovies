import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Spike Test Configuration - Sudden traffic bursts
export const options = {
  stages: [
    { duration: '30s', target: 20 },   // Normal load
    { duration: '10s', target: 300 },  // Sudden spike!
    { duration: '1m', target: 300 },   // Maintain spike
    { duration: '10s', target: 20 },   // Drop back to normal
    { duration: '30s', target: 20 },   // Recover
    { duration: '10s', target: 500 },  // Another bigger spike!
    { duration: '1m', target: 500 },   // Maintain
    { duration: '30s', target: 0 },    // Ramp down
  ],
  thresholds: {
    'http_req_duration': ['p(95)<1500'],
    'errors': ['rate<0.1'], // Allow up to 10% errors during spikes
  },
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // Simulate viral content access
  const movieId = Math.floor(Math.random() * 100);

  let res = http.get(`${BASE_URL}/movies`);
  check(res, {
    'GET /movies successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(0.3);

  res = http.get(`${BASE_URL}/movies/search?q=popular`);
  check(res, {
    'Search successful': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(0.2);
}

export function teardown(data) {
  console.log('Spike test completed!');
  console.log('Check how the system handled sudden traffic bursts.');
}

