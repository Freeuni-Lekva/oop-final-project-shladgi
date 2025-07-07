async function loadFriendsSection() {
    try {
        const response = await fetch("/user-friends", {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error("Failed to fetch friends: " + response.status);
        }

        const friends = await response.json();

        const friendsList = document.getElementById("friendsList");
        if (!friendsList) return;

        friendsList.innerHTML = ""; // Clear old list

        friends.forEach(friend => {
            const li = document.createElement("li");
            li.textContent = friend;
            friendsList.appendChild(li);
        });

    } catch (error) {
        console.error("Error loading friends:", error);
    }
}

function setupFriendsButtonListener() {
    const button = document.getElementById("friendsMenuItem");
    if (!button) return;

    button.addEventListener("click", async (e) => {
        e.preventDefault();

        const userSection = document.getElementById("user");
        const ratingSection = document.getElementById("rating");
        const friendsSection = document.getElementById("friends");

        if (userSection) userSection.style.display = "none";
        if (ratingSection) ratingSection.style.display = "none";
        if (friendsSection) friendsSection.style.display = "block";

        await loadFriendsSection();
    });
}

document.addEventListener("DOMContentLoaded", setupFriendsButtonListener);
