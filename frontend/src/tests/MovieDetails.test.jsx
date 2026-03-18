import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
jest.mock('react-router-dom', () => ({
  MemoryRouter: ({ children }) => <div>{children}</div>,
  Routes: ({ children }) => <div>{children}</div>,
  Route: ({ element }) => element,
  useParams: () => ({ id: '1' }),
}), { virtual: true });
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import MovieDetails from '../pages/MovieDetails';

jest.mock('../api/api', () => ({
  API: { get: jest.fn(), post: jest.fn() },
}));

const { API } = require('../api/api');

describe('MovieDetails', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  test('renders title and ranking from API', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText(/Iron Man/i)).toBeInTheDocument();
    expect(screen.getByText(/Xếp hạng:/i)).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  test('shows loading state initially', () => {
    API.get.mockImplementation(() => new Promise(() => {}));

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText(/Loading/i)).toBeInTheDocument();
  });

  test('shows error on API failure', async () => {
    API.get.mockRejectedValueOnce(new Error('API Error'));

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Failed to load movie/i));
    expect(screen.getByText(/Failed to load movie/i)).toBeInTheDocument();
  });

  test('handles add to favourites when logged in', async () => {
    localStorage.setItem('userId', 'user123');
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: 5,
      },
    });
    API.post.mockResolvedValueOnce({ status: 200 });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    
    const addButton = screen.getByText(/Thêm vào Yêu thích/i);
    fireEvent.click(addButton);

    await waitFor(() => screen.getByText(/Phim đã được thêm vào Yêu thích/i));
    expect(screen.getByText(/Phim đã được thêm vào Yêu thích/i)).toBeInTheDocument();
  });

  test('shows login message when not authenticated', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    
    const addButton = screen.getByText(/Thêm vào Yêu thích/i);
    fireEvent.click(addButton);

    await waitFor(() => screen.getByText(/Please login first/i));
    expect(screen.getByText(/Please login first/i)).toBeInTheDocument();
  });

  test('handles add to favourites error', async () => {
    localStorage.setItem('userId', 'user123');
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: 5,
      },
    });
    API.post.mockRejectedValueOnce(new Error('Error'));

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    
    const addButton = screen.getByText(/Thêm vào Yêu thích/i);
    fireEvent.click(addButton);

    await waitFor(() => screen.getByText(/Lỗi khi thêm vào Yêu thích/i));
    expect(screen.getByText(/Lỗi khi thêm vào Yêu thích/i)).toBeInTheDocument();
  });

  test('handles add to favourites non-200 response', async () => {
    localStorage.setItem('userId', 'user123');
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: 5,
      },
    });
    API.post.mockResolvedValueOnce({ status: 409 });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    
    const addButton = screen.getByText(/Thêm vào Yêu thích/i);
    fireEvent.click(addButton);

    await waitFor(() => screen.getByText(/Lỗi khi thêm vào Yêu thích/i));
    expect(screen.getByText(/Lỗi khi thêm vào Yêu thích/i)).toBeInTheDocument();
  });

  test('handles ranking as array', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: [{ ranking_name: '5' }],
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  test('handles ranking as object', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: { ranking_name: '5' },
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  test('handles missing ranking', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: 'Action',
        ranking: null,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText('N/A')).toBeInTheDocument();
  });

  test('handles genre as array', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: [{ genre_name: 'Action' }, { genre_name: 'Adventure' }],
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText(/Action, Adventure/i)).toBeInTheDocument();
  });

  test('handles genre as object', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: { genre_name: 'Action' },
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText('Action')).toBeInTheDocument();
  });

  test('handles missing genre', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Iron Man',
        poster_path: '/iron.jpg',
        genre: null,
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Iron Man/i));
    expect(screen.getByText(/Thể loại:/i)).toBeInTheDocument();
  });

  test('handles ranking as array with simple strings', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Test Movie',
        poster_path: '/test.jpg',
        genre: 'Action',
        ranking: ['5', '4'],
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Test Movie/i));
  });

  test('handles genre as array with simple strings', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Test Movie',
        poster_path: '/test.jpg',
        genre: ['Action', 'Drama'],
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Test Movie/i));
  });

  test('handles ranking object without ranking_name property', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Test Movie',
        poster_path: '/test.jpg',
        genre: 'Action',
        ranking: { value: '5' },
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Test Movie/i));
  });

  test('handles genre object without genre_name property', async () => {
    API.get.mockResolvedValueOnce({
      data: {
        id: '1',
        title: 'Test Movie',
        poster_path: '/test.jpg',
        genre: { value: 'Action' },
        ranking: 5,
      },
    });

    render(
      <MemoryRouter initialEntries={["/movie/1"]}>
        <Routes>
          <Route path="/movie/:id" element={<MovieDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText(/Test Movie/i));
  });
});
