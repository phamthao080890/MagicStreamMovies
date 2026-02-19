import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Register from '../pages/Register';

jest.mock('../api/api', () => ({
  API: { post: jest.fn() },
}));

const { API } = require('../api/api');

describe('Register page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('successful register shows success message', async () => {
    API.post.mockResolvedValueOnce({ status: 200 });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('status'));
    expect(screen.getByText(/Account created successfully/i)).toBeInTheDocument();
  });

  test('shows server error message when provided', async () => {
    API.post.mockRejectedValueOnce({ response: { data: { message: 'Email already exists' } } });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Email already exists/i)).toBeInTheDocument();
  });

  test('shows error on 400 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 400 } });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Invalid email or password/i)).toBeInTheDocument();
  });

  test('shows error on 403 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 403 } });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Your account is not authorized or locked/i)).toBeInTheDocument();
  });

  test('shows error on 429 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 429 } });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Too many attempts/i)).toBeInTheDocument();
  });

  test('shows error on 500 status', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 500 } });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Server error/i)).toBeInTheDocument();
  });

  test('shows network error on unknown error', async () => {
    API.post.mockRejectedValueOnce({ response: { status: 0 } });

    render(<Register />);

    fireEvent.change(screen.getByLabelText(/Tên/i), { target: { value: 'Tony' } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'tony@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu/i), { target: { value: 'secret' } });
    fireEvent.click(screen.getByRole('button', { name: /Đăng ký/i }));

    await waitFor(() => screen.getByRole('alert'));
    expect(screen.getByText(/Network or unexpected error/i)).toBeInTheDocument();
  });
});
