import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api";

export const API = axios.create({
    baseURL
});

// Optional: allow runtime override of base URL (e.g., tests or dynamic env)
export function setApiBaseUrl(url) {
    if (url) {
        API.defaults.baseURL = url;
    }
}