function loadName(){
    fetch("/user")
        .then(response => {
            if (!response.ok) {
                throw new Error("Network error: " + response.status);
            }
            return response.json();
        })
        .then(data => {
            const username = data.username || "Guest";
            const menuButton = document.getElementById("userMenuItem");
            if (menuButton) menuButton.textContent = username;
            const usernameDisplay = document.getElementById("usernameDisplay");
            if (usernameDisplay) usernameDisplay.textContent = username;
        })
        .catch(error => {
            console.error("Failed to load username:", error);
        });
}

window.addEventListener("DOMContentLoaded", loadName);

