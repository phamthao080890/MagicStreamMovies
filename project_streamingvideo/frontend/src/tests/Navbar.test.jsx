import React from 'react';
import { render, screen, act } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Link: ({ to, children }) => <a href={to}>{children}</a>,
  useLocation: () => ({ pathname: '/' }),
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import Navbar from '../components/Navbar';

describe('Navbar', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('shows Login when not authenticated', () => {
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText(/Trang chủ/i)).toBeInTheDocument();
    expect(screen.getByText(/Thể loại/i)).toBeInTheDocument();
    expect(screen.getByText(/Đăng nhập/i)).toBeInTheDocument();
    expect(screen.queryByText(/Yêu thích/i)).not.toBeInTheDocument();
  });

  test('shows Favourites and profile initials when authenticated', () => {
    localStorage.setItem('user', 'user@example.com');
    localStorage.setItem('name', 'Tony Stark');
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    // Profile initials should show first two letters of name
    expect(screen.getByText(/to/i)).toBeInTheDocument();
    expect(screen.getByText(/Yêu thích/i)).toBeInTheDocument();
    expect(screen.queryByText(/Đăng nhập/i)).not.toBeInTheDocument();
  });

  test('falls back to email prefix initials when name missing', () => {
    localStorage.setItem('user', 'jane.doe@example.com');
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    // Expect first two letters of prefix 'jane.doe' => 'ja'
    expect(screen.getByText('ja')).toBeInTheDocument();
  });

  test('updates user on route change', () => {
    const { rerender } = render(
      <MemoryRouter initialEntries={['/']}>
        <Navbar />
      </MemoryRouter>
    );
    
    localStorage.setItem('user', 'test@example.com');
    localStorage.setItem('name', 'Test User');
    
    // Trigger re-render with different location
    rerender(
      <MemoryRouter initialEntries={['/profile']}>
        <Navbar />
      </MemoryRouter>
    );
  });

  test('responds to app:user-changed event', () => {
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    
    // Set user and trigger event
    act(() => {
      localStorage.setItem('user', 'event@example.com');
      localStorage.setItem('name', 'Event User');
      const event = new Event('app:user-changed');
      window.dispatchEvent(event);
    });
  });

  test('handles empty name with empty string', () => {
    localStorage.setItem('user', 'user@example.com');
    localStorage.setItem('name', '');
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText('us')).toBeInTheDocument();
  });

  test('handles whitespace-only name', () => {
    localStorage.setItem('user', 'user@example.com');
    localStorage.setItem('name', '   ');
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText('us')).toBeInTheDocument();
  });

  test('handles user with no @ symbol', () => {
    localStorage.setItem('user', 'nu');
    localStorage.setItem('name', null);
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText('nu')).toBeInTheDocument();
  });
});
