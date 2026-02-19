import { loginUser, logoutUser, getUser } from '../utils/auth';

describe('auth utils', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.spyOn(window, 'dispatchEvent').mockImplementation(() => true);
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('loginUser stores user and dispatches event', () => {
    loginUser('user@example.com');
    expect(localStorage.getItem('user')).toBe('user@example.com');
    expect(window.dispatchEvent).toHaveBeenCalled();
  });

  test('getUser returns current user', () => {
    localStorage.setItem('user', 'a@b.com');
    expect(getUser()).toBe('a@b.com');
  });

  test('logoutUser clears auth keys and dispatches event', () => {
    localStorage.setItem('user', 'a@b.com');
    localStorage.setItem('userId', '123');
    localStorage.setItem('token', 'tok');
    localStorage.setItem('name', 'Tony');
    logoutUser();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('userId')).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('name')).toBeNull();
    expect(window.dispatchEvent).toHaveBeenCalled();
  });

  test('loginUser handles event dispatch error gracefully', () => {
    window.dispatchEvent.mockImplementation(() => {
      throw new Error('Event error');
    });
    expect(() => loginUser('test@example.com')).not.toThrow();
    expect(localStorage.getItem('user')).toBe('test@example.com');
  });

  test('logoutUser handles event dispatch error gracefully', () => {
    localStorage.setItem('user', 'test@example.com');
    window.dispatchEvent.mockImplementation(() => {
      throw new Error('Event error');
    });
    expect(() => logoutUser()).not.toThrow();
    expect(localStorage.getItem('user')).toBeNull();
  });
});
