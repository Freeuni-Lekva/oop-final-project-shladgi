document.addEventListener("DOMContentLoaded", () => {
    // Assuming the username is set in the #userMenuItem button text by loadFirstTimeName.js
    const username = document.getElementById("userMenuItem")?.textContent.trim();

    if (!username || username === "NAME") {
        console.error("Username not found or not set yet.");
        return;
    }

    fetch("user-friend-request", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `username=${encodeURIComponent(username)}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Network response was not ok: ${response.status}`);
            }
            return response.json();
        })
        .then(usernames => {
            const container = document.getElementById("friend-requests-container");
            container.innerHTML = "";  // Clear previous content

            if (usernames.length === 0) {
                container.textContent = "No friend requests.";
                return;
            }

            usernames.forEach(name => {
                const div = document.createElement("div");
                div.className = "friend-request alert alert-info";
                div.textContent = `${name} sent you a friend request.`;
                container.appendChild(div);
            });
        })
        .catch(error => {
            console.error("Failed to load friend requests:", error);
        });
});
