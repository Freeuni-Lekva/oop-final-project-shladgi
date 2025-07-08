import { loadSessionValue } from "./getSessionInfo.js";
window.addEventListener("DOMContentLoaded", async () => {
    const userid = await loadSessionValue("userid");
    const welcome = document.getElementById("welcomeMsg");

    if (userid) {
        welcome.textContent = `Welcome to home page, user #${userid}!`;
    } else {
        welcome.textContent = "Welcome to home page, guest!";
    }
});
