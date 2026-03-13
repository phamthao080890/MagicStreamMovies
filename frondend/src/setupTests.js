// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';
import React from 'react';

// Global mock for react-router-dom to avoid ESM resolver issues in Jest
jest.mock('react-router-dom', () => {
	const navigateMock = jest.fn();
	const RouterStub = ({ children }) => <div>{children}</div>;
	const LinkStub = ({ to, children }) => <a href={to}>{children}</a>;
	return {
		__esModule: true,
		BrowserRouter: RouterStub,
		MemoryRouter: RouterStub,
		Routes: RouterStub,
		Route: ({ element, children }) => element || <div>{children}</div>,
		Link: LinkStub,
		useNavigate: () => navigateMock,
		useLocation: () => ({ pathname: '/' }),
		useParams: () => ({ id: '1' }),
		default: {
			Link: LinkStub,
			useNavigate: () => navigateMock,
			useLocation: () => ({ pathname: '/' }),
		},
	};
}, { virtual: true });

// Mock axios to sidestep ESM import in Jest
jest.mock('axios', () => ({
	__esModule: true,
	default: { create: () => ({ get: jest.fn(), post: jest.fn() }) },
	create: () => ({ get: jest.fn(), post: jest.fn() }),
}), { virtual: true });

// jsdom's scrollTo either missing or not implemented; stub it for tests
// eslint-disable-next-line no-undef
window.scrollTo = jest.fn();
