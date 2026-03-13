//Save user Login
export function loginUser(email) {
    localStorage.setItem("user", email);
    // Notify app components that user changed
    try { window.dispatchEvent(new Event("app:user-changed")); } catch {}
}

//Remove Login
export function logoutUser() {
    localStorage.removeItem("user");
    localStorage.removeItem("userId");
    localStorage.removeItem("token");
    localStorage.removeItem("name");
    // Notify app components that user changed
    try { window.dispatchEvent(new Event("app:user-changed")); } catch {}
}

//Get current Logged-in user
export function getUser() {
    return localStorage.getItem("user");
}
