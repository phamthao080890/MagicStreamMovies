import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  useNavigate: () => mockNavigate,
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Profile from '../pages/Profile';

jest.mock('../utils/auth', () => ({
  getUser: jest.fn(),
  logoutUser: jest.fn(),
}));

const { getUser, logoutUser } = require('../utils/auth');

describe('Profile page', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  test('shows name and email when logged in', () => {
    getUser.mockReturnValue('tony@example.com');
    localStorage.setItem('name', 'Tony');

    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );

    expect(screen.getByText(/Hồ sơ/i)).toBeInTheDocument();
    expect(screen.getByText(/Tên:/i)).toBeInTheDocument();
    expect(screen.getAllByText(/Tony/i)[0]).toBeInTheDocument();
    expect(screen.getByText(/Email:/i)).toBeInTheDocument();
    expect(screen.getByText(/tony@example.com/i)).toBeInTheDocument();
  });

  test('redirects to login when not authenticated', () => {
    getUser.mockReturnValue(null);

    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );

    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  test('handles logout correctly', () => {
    getUser.mockReturnValue('tony@example.com');
    localStorage.setItem('name', 'Tony');

    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );

    const logoutButton = screen.getByText(/Đăng xuất/i);
    fireEvent.click(logoutButton);

    expect(logoutUser).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  test('handles missing name with fallback to email prefix', () => {
    getUser.mockReturnValue('john.doe@example.com');

    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );

    expect(screen.getAllByText(/john.doe/i).length).toBeGreaterThan(0);
  });

  test('handles missing name and user', () => {
    getUser.mockReturnValue('username');
    localStorage.removeItem('name');

    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );

    expect(screen.getAllByText(/username/i).length).toBeGreaterThan(0);
  });
});
