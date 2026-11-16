import React, { useState } from "react";
import { API } from "../api/api";
import MovieCard from "../components/MovieCard";
import "../components/MovieCard.css";
import "./Featured.css";

export default function Featured() {
    const [query, setQuery] = useState("");
    const [submitted, setSubmitted] = useState("");
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const recommend = async (event) => {
        event.preventDefault();
        const words = query.trim();
        if (!words || loading) return;
        setSubmitted(words);
        setLoading(true);
        setError("");
        setMovies([]);
        try {
            const response = await API.get("/recommendations", { params: { q: words } });
            setMovies(response.data);
        } catch {
            setError("Không thể tải gợi ý lúc này. Vui lòng thử lại sau.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="featured">
            <section className="featured__intro">
                <p className="featured__eyebrow">KHÁM PHÁ PHIM</p>
                <h1>Nổi bật</h1>
                <p>Hôm nay bạn muốn xem gì? Mô tả tâm trạng, thể loại hoặc câu chuyện bạn muốn xem để nhận gợi ý từ thư viện phim.</p>
                <form className="featured__form" onSubmit={recommend}>
                    <label htmlFor="recommendation-query">Từ khóa của bạn</label>
                    <div className="featured__controls">
                        <input id="recommendation-query" value={query} onChange={e => setQuery(e.target.value)} placeholder="Ví dụ: phim hành động gay cấn về một cuộc truy đuổi" maxLength={500} />
                        <button type="submit" disabled={loading || !query.trim()}>Gợi ý phim</button>
                    </div>
                </form>
            </section>
            {!submitted && <div className="featured__hint"><span className="featured__hint-icon" aria-hidden="true">✦</span><div><strong>Chưa biết xem gì?</strong><p>Thử nhập “phim tình cảm nhẹ nhàng” hoặc “hành động gay cấn” để bắt đầu.</p></div></div>}
            <section className="featured__results" aria-live="polite">
                {loading && <p>Đang tìm phim phù hợp...</p>}
                {error && <p role="alert">{error}</p>}
                {!loading && !error && submitted && <h2>Gợi ý cho “{submitted}”</h2>}
                {!loading && !error && submitted && movies.length === 0 && <p>Chưa tìm thấy phim phù hợp. Hãy thử mô tả khác.</p>}
                <div className="cards-grid">{movies.map(movie => <MovieCard key={movie.id} movie={movie} />)}</div>
            </section>
        </main>
    );
}
