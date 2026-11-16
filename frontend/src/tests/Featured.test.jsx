import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Featured from "../pages/Featured";
import { API } from "../api/api";

jest.mock("../api/api", () => ({ API: { get: jest.fn() } }));

test("requests and displays recommendations for the entered words", async () => {
    API.get.mockResolvedValueOnce({ data: [{ id: "1", title: "Action Hero", poster_path: "poster.jpg" }] });
    render(<MemoryRouter><Featured /></MemoryRouter>);
    fireEvent.change(screen.getByLabelText("Từ khóa của bạn"), { target: { value: "hành động" } });
    fireEvent.click(screen.getByRole("button", { name: "Gợi ý phim" }));
    expect(API.get).toHaveBeenCalledWith("/recommendations", { params: { q: "hành động" } });
    expect(await screen.findByText("Action Hero")).toBeInTheDocument();
});

test("shows a helpful empty result", async () => {
    API.get.mockResolvedValueOnce({ data: [] });
    render(<MemoryRouter><Featured /></MemoryRouter>);
    fireEvent.change(screen.getByLabelText("Từ khóa của bạn"), { target: { value: "unknown" } });
    fireEvent.click(screen.getByRole("button", { name: "Gợi ý phim" }));
    await waitFor(() => expect(screen.getByText(/Chưa tìm thấy phim phù hợp/)).toBeInTheDocument());
});
