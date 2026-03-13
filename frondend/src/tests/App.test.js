import React from 'react';
import { render, screen } from '@testing-library/react';
import App from '../App';
import AppJsx from '../App.jsx';

jest.mock('../api/api', () => ({
  API: { 
    get: jest.fn(),
    post: jest.fn(),
  },
}));

const { API } = require('../api/api');

// Suppress console errors for act warnings in these tests
const originalError = console.error;
beforeAll(() => {
  console.error = (...args) => {
    if (
      typeof args[0] === 'string' &&
      args[0].includes('Warning: An update')
    ) {
      return;
    }
    originalError.call(console, ...args);
  };
});

afterAll(() => {
  console.error = originalError;
});

describe('App components', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    API.get.mockResolvedValue({ data: [] });
    API.post.mockResolvedValue({ data: [] });
  });

  test('App.js component is defined and renders', () => {
    expect(App).toBeTruthy();
    render(<App />);
    // App renders without crashing
  });

  test('App.jsx component is defined and renders', () => {
    expect(AppJsx).toBeTruthy();
    render(<AppJsx />);
    // AppJsx renders without crashing
  });
});
