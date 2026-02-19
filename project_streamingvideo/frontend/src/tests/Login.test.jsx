import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  useNavigate: () => jest.fn(),
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Login from '../pages/Login';

jest.mock('../api/api', () => ({
  API: { post: jest.fn() },
}));

const { API } = require('../api/api');

describe('Login page', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  test('successful login stores auth and shows success', async () => {
    API.post.mockResolvedValueOnce({
      data: { token: 'tok123', userId: 'u1', name: 'Tony' },
    });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => expect(API.post).toHaveBeenCalled());
    await waitFor(() => screen.getByText(/Login successful/i));
    expect(localStorage.getItem('token')).toBe('tok123');
    expect(localStorage.getItem('userId')).toBe('u1');
    expect(localStorage.getItem('name')).toBe('Tony');
    expect(localStorage.getItem('email')).toBe('tony@example.com');
    expect(screen.getByText(/Login successful/i)).toBeInTheDocument();
  });

  test('shows error on invalid credentials', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 401 } });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'bad@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'wrong' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Invalid email or password/i)).toBeInTheDocument();
  });

  test('shows error on 400 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 400 } });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'bad@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'wrong' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Invalid email or password/i)).toBeInTheDocument();
  });

  test('shows error on 403 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 403 } });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'pass' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Your account is not authorized or locked/i)).toBeInTheDocument();
  });

  test('shows error on 429 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 429 } });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'pass' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Too many attempts/i)).toBeInTheDocument();
  });

  test('shows error on 500 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 500 } });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'pass' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Server error/i)).toBeInTheDocument();
  });

  test('shows network error on unknown error', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 0 } });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'pass' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Network or unexpected error/i)).toBeInTheDocument();
  });

  test('handles email without @ symbol for name derivation', async () => {
    API.post.mockResolvedValueOnce({
      data: { token: 'tok123', userId: 'u1' },
    });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'username' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'pass' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => expect(API.post).toHaveBeenCalled());
    expect(localStorage.getItem('name')).toBe('username');
  });

  test('handles response with nested user.name', async () => {
    API.post.mockResolvedValueOnce({
      data: { token: 'tok123', userId: 'u1', user: { name: 'Nested User' } },
    });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'pass' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng nhập/i }));

    await waitFor(() => expect(API.post).toHaveBeenCalled());
    expect(localStorage.getItem('name')).toBe('Nested User');
  });
});
