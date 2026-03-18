import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children }) => <a href={to}>{children}</a>,
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Home from '../pages/Home';

jest.mock('../api/api', () => ({
  API: { get: jest.fn() },
}));

const { API } = require('../api/api');

describe('Home page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('fetches and displays movies', async () => {
    API.get.mockResolvedValueOnce({
      data: [
        { id: 1, title: 'Movie 1', poster_path: '/path1.jpg' },
        { id: 2, title: 'Movie 2', poster_path: '/path2.jpg' },
      ],
    });

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Movie 1'));
    expect(screen.getByText('Movie 1')).toBeInTheDocument();
    expect(screen.getByText('Movie 2')).toBeInTheDocument();
  });

  test('handles API error gracefully', async () => {
    const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
    API.get.mockRejectedValueOnce(new Error('API Error'));

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    await waitFor(() => expect(API.get).toHaveBeenCalled());
    expect(consoleLogSpy).toHaveBeenCalled();
    consoleLogSpy.mockRestore();
  });
});
