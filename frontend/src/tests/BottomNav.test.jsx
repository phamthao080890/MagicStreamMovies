import React from 'react';
import { render, screen } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children, title }) => <a href={to} title={title}>{children}</a>,
  useLocation: () => ({ pathname: '/' }),
}), { virtual: true });
import { MemoryRouter, useLocation } from 'react-router-dom';
import BottomNav from '../components/BottomNav';

jest.mock('../utils/auth', () => ({
  getUser: jest.fn(),
}));

const { getUser } = require('../utils/auth');

describe('BottomNav', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('returns null when not authenticated', () => {
    getUser.mockReturnValue(null);
    render(
      <MemoryRouter>
        <BottomNav />
      </MemoryRouter>
    );
    expect(screen.queryByTitle(/Yêu thích/i)).not.toBeInTheDocument();
  });

  test('shows favourites link when authenticated', () => {
    getUser.mockReturnValue('user@example.com');
    render(
      <MemoryRouter>
        <BottomNav />
      </MemoryRouter>
    );
    expect(screen.getByTitle(/Yêu thích/i)).toBeInTheDocument();
  });
});
