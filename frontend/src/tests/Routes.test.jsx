import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import App from "../App";
import { API } from "../api/api";

jest.mock("react-router-dom", () => ({
    BrowserRouter: ({ children }) => <div>{children}</div>,
    Routes: ({ children }) => <div>{children}</div>,
    Route: ({ path, element }) => path === globalThis.location.pathname ? element : null,
    Link: ({ to, children }) => <a href={to}>{children}</a>,
    useLocation: () => ({ pathname: globalThis.location.pathname }),
    useNavigate: () => jest.fn(),
    useParams: () => ({}),
    useSearchParams: () => [new URLSearchParams()],
}), { virtual: true });
jest.mock("../api/api", () => ({ API: { get: jest.fn(), post: jest.fn() } }));
beforeEach(() => { API.get.mockResolvedValue({ data: [] }); window.scrollTo = jest.fn(); });
test("root shows recommendations and navigation points to all movies", () => {
    window.history.pushState({}, "", "/");
    render(<App />);
    expect(screen.getByRole("heading", { name: "Nổi bật" })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Tất cả" })).toHaveAttribute("href", "/all");
});
test("all route shows the movie catalog", async () => {
    window.history.pushState({}, "", "/all");
    render(<App />);
    expect(screen.getByRole("heading", { name: "Tất cả phim" })).toBeInTheDocument();
    await waitFor(() => expect(API.get).toHaveBeenCalledWith("/movies"));
});
