import http from 'k6/http';
import { check, sleep } from 'k6';

// Simple smoke test - verify API is working
export const options = {
  vus: 5,
  duration: '30s',
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // Test basic endpoints
  let res = http.get(`${BASE_URL}/movies`);
  check(res, {
    'GET /movies returns 200': (r) => r.status === 200,
  });

  sleep(1);
}

export function setup() {
  console.log('===========================================');
  console.log('Smoke Test - Verifying API is accessible');
  console.log('===========================================');

  // Test if API is reachable
  const res = http.get(`${BASE_URL}/movies`);
  if (res.status === 0) {
    console.error('ERROR: Cannot connect to API at', BASE_URL);
    console.error('Please ensure:');
    console.error('1. API is running: mvn spring-boot:run');
    console.error('2. API is accessible at http://localhost:8080');
    throw new Error('API not accessible');
  }

  console.log('✓ API is accessible');
  console.log('Base URL:', BASE_URL);
  console.log('');
}

export function teardown(data) {
  console.log('');
  console.log('Smoke test completed successfully!');
  console.log('Your API is ready for load testing.');
}

