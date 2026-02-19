// Import fresh for api tests
jest.resetModules();

describe('API module - basic functionality', () => {
  test('exports API axios instance', () => {
    const apiModule = require('../api/api');
    expect(apiModule.API).toBeDefined();
    expect(apiModule.setApiBaseUrl).toBeDefined();
  });

  test('API has expected axios methods', () => {
    const { API } = require('../api/api');
    expect(typeof API.get).toBe('function');
    expect(typeof API.post).toBe('function');
  });

  test('setApiBaseUrl is a function', () => {
    const { setApiBaseUrl } = require('../api/api');
    expect(typeof setApiBaseUrl).toBe('function');
  });
});
