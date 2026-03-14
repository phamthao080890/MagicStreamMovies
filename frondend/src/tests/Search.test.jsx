import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

const mockUseSearchParams = jest.fn();
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children }) => <a href={to}>{children}</a>,
  useSearchParams: () => mockUseSearchParams(),
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Search from '../pages/Search';

jest.mock('../api/api', () => ({
  API: { get: jest.fn() },
}));

const { API } = require('../api/api');

describe('Search page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders search results page', () => {
    mockUseSearchParams.mockReturnValue([new URLSearchParams()]);
    render(
      <MemoryRouter>
        <Search />
      </MemoryRouter>
    );

    expect(screen.getByText('Kết quả tìm kiếm')).toBeInTheDocument();
  });

  test('displays movies when search returns results', async () => {
    const searchParams = new URLSearchParams('q=Inception');
    mockUseSearchParams.mockReturnValue([searchParams]);

    API.get.mockResolvedValueOnce({
      data: [
        { id: 1, title: 'Inception', poster_path: '/path1.jpg' },
        { id: 2, title: 'Interstellar', poster_path: '/path2.jpg' },
      ],
    });

    render(
      <MemoryRouter>
        <Search />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Inception'));
    expect(screen.getByText('Inception')).toBeInTheDocument();
    expect(screen.getByText('Interstellar')).toBeInTheDocument();
    expect(API.get).toHaveBeenCalledWith('/movies/search?q=Inception');
  });

  test('handles API error gracefully', async () => {
    const searchParams = new URLSearchParams('q=Nonexistent Movie');
    mockUseSearchParams.mockReturnValue([searchParams]);

    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    API.get.mockRejectedValueOnce(new Error('API Error'));

    render(
      <MemoryRouter>
        <Search />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Failed to search movies. Please try again.'));
    expect(screen.getByText('Failed to search movies. Please try again.')).toBeInTheDocument();
    expect(consoleErrorSpy).toHaveBeenCalled();
    consoleErrorSpy.mockRestore();
  });

  test('shows no results message', async () => {
    const searchParams = new URLSearchParams('q=Unknown Movie');
    mockUseSearchParams.mockReturnValue([searchParams]);

    API.get.mockResolvedValueOnce({
      data: [],
    });

    render(
      <MemoryRouter>
        <Search />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/KHÔNG TÌM THẤY KẾT QUẢ CHO/));
    expect(screen.getByText('KHÔNG TÌM THẤY KẾT QUẢ CHO')).toBeInTheDocument();
    expect(screen.getByText('"Unknown Movie"')).toBeInTheDocument();
  });

  test('does not perform search with empty query', () => {
    const searchParams = new URLSearchParams('q=');
    mockUseSearchParams.mockReturnValue([searchParams]);

    render(
      <MemoryRouter>
        <Search />
      </MemoryRouter>
    );

    expect(API.get).not.toHaveBeenCalled();
  });
});