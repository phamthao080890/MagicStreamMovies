# Streaming App Frontend

React app for browsing movies, genres, favourites, and viewing details with authentication (login/register/profile). Built with Create React App.

## Overview

- Home grid of movies using responsive cards
- Genres page with deduped, selectable genres
- Movie details with poster and trailer
- Auth flow: Login, Register, Profile with logout
- Favourites page (requires login)

Key files:
- API client: [src/api/api.js](src/api/api.js)
- Router + pages: [src/App.js](src/App.js)
- Navbar: [src/components/Navbar.jsx](src/components/Navbar.jsx)
- Auth helpers: [src/utils/auth.js](src/utils/auth.js)

## Prerequisites

- Node.js LTS (18+ recommended)
- npm (comes with Node.js)

## Setup

```bash
npm install
```

If your backend runs on a different host or port, update the API base URL in [src/api/api.js](src/api/api.js):

```javascript
export const API = axios.create({
	baseURL: "http://localhost:8080/api",
});
```

### Environment configuration (.env)

Create a `.env.local` file to override the API base URL:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

You can keep different files per environment:
- `.env.development.local` for local dev
- `.env.test.local` for tests
- `.env.production.local` for production builds

CRA loads variables prefixed with `REACT_APP_`. Do not store secrets in `.env`.

Alternatively, for dev proxying, you can add a `proxy` field to [package.json](package.json):

```json
{
	"proxy": "http://localhost:8080"
}
```

## Run (Development)

```bash
npm start
```

- Opens at http://localhost:3000
- Hot reload on changes

## Test

```bash
npm test
```

- Runs Jest + Testing Library
- Non-interactive run: `npm test -- --watchAll=false`
- Global test setup: [src/setupTests.js](src/setupTests.js)
	- Mocks `react-router-dom` and `axios` for unit tests
	- Stubs `window.scrollTo` used by `ScrollToTop`
	- Honors `REACT_APP_API_BASE_URL` when creating the API client
 - Test files live under [src/tests](src/tests) to keep source and tests organized
 - Common commands:
	 - Watch mode: `npm test`
	 - Single run: `npm test -- --watchAll=false`
	 - Coverage: `npm test -- --coverage`

## Build (Production)

```bash
npm run build
```

- Outputs to [build/](build)
- Minified assets with content hashes

## Deploy

Quick local static serve:

```bash
npm install -g serve
serve -s build
```

Static hosting options:
- Netlify / Vercel: deploy the `build/` folder
- GitHub Pages: serve `build/` via a static server
- Any static web server: point to `build/`

Note: Ensure API base URL is reachable from your hosting environment (update [src/api/api.js](src/api/api.js) if needed).

## Project Structure (simplified)

```
src/
	api/
		api.js
	components/
		Navbar.jsx
		MovieCard.jsx
		ScrollToTop.jsx
		BottomNav.jsx
	pages/
		Home.jsx
		Genres.jsx
		MovieDetails.jsx
		Favourites.jsx
		Login.jsx
		Register.jsx
		Profile.jsx
	utils/
		auth.js
	App.js
	setupTests.js
```

## Troubleshooting

- Tests failing with router or axios import issues: verify global mocks in [src/setupTests.js](src/setupTests.js) remain intact.
- API errors: confirm backend at `http://localhost:8080/api` or adjust base URL.
- Blank page after login: ensure localStorage keys are set (`user`, `token`, `userId`, `name`) and that Navbar listens to `app:user-changed` events.

## Scripts

- `npm start` — dev server
- `npm test` — unit tests (watch mode)
- `npm run build` — production build
- `npm run eject` — CRA eject (irreversible)
