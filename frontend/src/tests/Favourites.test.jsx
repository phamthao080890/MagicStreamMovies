import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children }) => <a href={to}>{children}</a>,
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Favourites from '../pages/Favourites';

jest.mock('../api/api', () => ({
  API: { get: jest.fn() },
}));

const { API } = require('../api/api');

describe('Favourites page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('shows login message when not authenticated', async () => {
    render(
      <MemoryRouter>
        <Favourites />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Vui lòng đăng nhập/i));
    expect(screen.getByText(/Vui lòng đăng nhập/i)).toBeInTheDocument();
  });

  test('shows empty message when no favourites', async () => {
    localStorage.setItem('userId', 'user123');
    API.get.mockResolvedValueOnce({ data: [] });

    render(
      <MemoryRouter>
        <Favourites />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Bạn chưa có phim yêu thích nào/i));
    expect(screen.getByText(/Bạn chưa có phim yêu thích nào/i)).toBeInTheDocument();
  });

  test('displays favourite movies when available', async () => {
    localStorage.setItem('userId', 'user123');
    API.get.mockResolvedValueOnce({ data: [1, 2] });
    API.get.mockResolvedValueOnce({
      data: { id: 1, title: 'Fav Movie 1', poster_path: '/fav1.jpg' },
    });
    API.get.mockResolvedValueOnce({
      data: { id: 2, title: 'Fav Movie 2', poster_path: '/fav2.jpg' },
    });

    render(
      <MemoryRouter>
        <Favourites />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Fav Movie 1'));
    expect(screen.getByText('Fav Movie 1')).toBeInTheDocument();
    expect(screen.getByText('Fav Movie 2')).toBeInTheDocument();
  });

  test('handles API error gracefully', async () => {
    localStorage.setItem('userId', 'user123');
    API.get.mockRejectedValueOnce(new Error('API Error'));

    render(
      <MemoryRouter>
        <Favourites />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Lỗi khi tải phim yêu thích/i));
    expect(screen.getByText(/Lỗi khi tải phim yêu thích/i)).toBeInTheDocument();
  });
});
