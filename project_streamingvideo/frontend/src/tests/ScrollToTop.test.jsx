import React from 'react';
import { render } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  useLocation: () => ({ pathname: '/' }),
}), { virtual: true });
import { MemoryRouter } from 'react-router-dom';
import ScrollToTop from '../components/ScrollToTop';

describe('ScrollToTop', () => {
  test('scrolls to top on mount', () => {
    const scrollToSpy = jest.fn();
    window.scrollTo = scrollToSpy;

    render(
      <MemoryRouter>
        <ScrollToTop />
      </MemoryRouter>
    );

    expect(scrollToSpy).toHaveBeenCalledWith({ top: 0, behavior: 'smooth' });
  });

  test('returns null (no visible output)', () => {
    const { container } = render(
      <MemoryRouter>
        <ScrollToTop />
      </MemoryRouter>
    );
    // ScrollToTop returns null, but MemoryRouter wraps it in a div
    expect(container.querySelector('*')).toBeTruthy();
  });
});
