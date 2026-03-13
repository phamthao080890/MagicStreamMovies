import axios from "axios";
import config from "../config";

const baseURL = config.apiBaseUrl + "/api";

export const API = axios.create({
    baseURL
});

// Optional: allow runtime override of base URL (e.g., tests or dynamic env)
export function setApiBaseUrl(url) {
    if (url) {
        API.defaults.baseURL = url;
    }
}