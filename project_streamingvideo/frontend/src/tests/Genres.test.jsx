import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children }) => <a href={to}>{children}</a>,
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Genres from '../pages/Genres';

jest.mock('../api/api', () => ({
  API: { post: jest.fn(), get: jest.fn() },
}));

const { API } = require('../api/api');

describe('Genres page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('loads genres and first genre movies on mount', async () => {
    API.post.mockResolvedValueOnce({
      data: [
        { id: 1, genre_name: 'Action' },
        { id: 2, genre_name: 'Comedy' },
      ],
    });
    API.get.mockResolvedValueOnce({
      data: [{ id: 101, title: 'Action Movie', poster_path: '/action.jpg' }],
    });

    render(
      <MemoryRouter>
        <Genres />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Action'));
    expect(screen.getByText('Action')).toBeInTheDocument();
    expect(screen.getByText('Comedy')).toBeInTheDocument();
    expect(screen.getByText('Action Movie')).toBeInTheDocument();
  });

  test('switches genre on button click', async () => {
    API.post.mockResolvedValueOnce({
      data: [
        { id: 1, genre_name: 'Action' },
        { id: 2, genre_name: 'Comedy' },
      ],
    });
    API.get.mockResolvedValueOnce({
      data: [{ id: 101, title: 'Action Movie', poster_path: '/action.jpg' }],
    });

    render(
      <MemoryRouter>
        <Genres />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Comedy'));

    API.get.mockResolvedValueOnce({
      data: [{ id: 201, title: 'Comedy Movie', poster_path: '/comedy.jpg' }],
    });

    fireEvent.click(screen.getByText('Comedy'));

    await waitFor(() => screen.getByText('Comedy Movie'));
    expect(screen.getByText('Comedy Movie')).toBeInTheDocument();
  });

  test('handles empty genres list', async () => {
    API.post.mockResolvedValueOnce({ data: [] });

    render(
      <MemoryRouter>
        <Genres />
      </MemoryRouter>
    );

    await waitFor(() => expect(API.post).toHaveBeenCalled());
  });

  test('handles API errors', async () => {
    const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
    API.post.mockRejectedValueOnce(new Error('Failed'));

    render(
      <MemoryRouter>
        <Genres />
      </MemoryRouter>
    );

    await waitFor(() => expect(consoleLogSpy).toHaveBeenCalled());
    consoleLogSpy.mockRestore();
  });

  test('handles genre movie fetch error', async () => {
    const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
    API.post.mockResolvedValueOnce({
      data: [{ id: 1, genre_name: 'Action' }],
    });
    API.get.mockRejectedValueOnce(new Error('Failed'));

    render(
      <MemoryRouter>
        <Genres />
      </MemoryRouter>
    );

    await waitFor(() => expect(consoleLogSpy).toHaveBeenCalled());
    consoleLogSpy.mockRestore();
  });

  test('handles loadMoviesByGenre error on button click', async () => {
    const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
    API.post.mockResolvedValueOnce({
      data: [
        { id: 1, genre_name: 'Action' },
        { id: 2, genre_name: 'Comedy' },
      ],
    });
    API.get.mockResolvedValueOnce({
      data: [{ id: 101, title: 'Action Movie', poster_path: '/action.jpg' }],
    });

    render(
      <MemoryRouter>
        <Genres />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Comedy'));

    API.get.mockRejectedValueOnce(new Error('Genre fetch failed'));
    fireEvent.click(screen.getByText('Comedy'));

    await waitFor(() => expect(consoleLogSpy).toHaveBeenCalled());
    consoleLogSpy.mockRestore();
  });
});
