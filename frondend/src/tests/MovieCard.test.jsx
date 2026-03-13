import React from 'react';
import { render, screen } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children }) => <a href={to}>{children}</a>,
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import MovieCard from '../components/MovieCard';

describe('MovieCard', () => {
  const movie = {
    id: 42,
    title: 'The Answer',
    poster_path: '/poster.jpg',
  };

  test('renders title and poster image', () => {
    render(
      <MemoryRouter>
        <MovieCard movie={movie} />
      </MemoryRouter>
    );
    expect(screen.getByText(/The Answer/i)).toBeInTheDocument();
    const img = screen.getByAltText(/The Answer/i);
    expect(img).toHaveAttribute('src', '/poster.jpg');
  });

  test('wraps with link to movie details', () => {
    render(
      <MemoryRouter>
        <MovieCard movie={movie} />
      </MemoryRouter>
    );
    const link = screen.getByRole('link');
    expect(link).toHaveAttribute('href', '/movie/42');
  });
});
