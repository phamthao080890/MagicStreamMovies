package com.streaming.api.services;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

public final class GenreNames {
    private static final Map<String, String> ALIASES = Map.ofEntries(
        Map.entry("action", "Hành Động"), Map.entry("comedy", "Hài Hước"), Map.entry("hài", "Hài Hước"),
        Map.entry("drama", "Chính kịch"), Map.entry("fantasy", "Giả Tưởng"),
        Map.entry("thriller", "Gây Cấn"), Map.entry("sci-fi", "Khoa Học Viễn Tưởng"),
        Map.entry("viễn tưởng", "Khoa Học Viễn Tưởng"), Map.entry("mystery", "Bí ẩn"),
        Map.entry("crime", "Hình Sự"), Map.entry("western", "Miền Tây"),
        Map.entry("lãng mạn", "Tình Cảm"));
    private GenreNames() {}
    public static String canonical(String value) {
        if (value == null) return "";
        String cleaned = Normalizer.normalize(value.trim(), Normalizer.Form.NFC);
        return ALIASES.getOrDefault(cleaned.toLowerCase(Locale.ROOT), cleaned);
    }
    public static boolean matches(String first, String second) {
        return canonical(first).equalsIgnoreCase(canonical(second));
    }
}
