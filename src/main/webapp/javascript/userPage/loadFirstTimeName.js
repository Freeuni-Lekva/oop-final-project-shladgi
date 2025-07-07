document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const username = params.get("username") || "Guest";

    const menuItem = document.getElementById("userMenuItem");
    if (menuItem) {
        menuItem.textContent = username;
    }

    const usernameDisplay = document.getElementById("usernameDisplay");
    if (usernameDisplay) {
        usernameDisplay.textContent = username;
    }
});
