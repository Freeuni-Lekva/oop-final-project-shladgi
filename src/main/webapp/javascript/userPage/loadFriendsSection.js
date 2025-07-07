async function loadFriendsSection(username) {
    try {
        const response = await fetch("/user-friends", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `username=${encodeURIComponent(username)}`
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
    const username = document.getElementById("userMenuItem").textContent;
    const button = document.getElementById("friendsMenuItem");
    if (!button) return;

    button.addEventListener("click", async (e) => {
        e.preventDefault();
        document.getElementById("user").style.display = "none";
        document.getElementById("statistics").style.display = "none";
        document.getElementById("friends").style.display = "block";

        await loadFriendsSection(username);
    });
}

document.addEventListener("DOMContentLoaded", setupFriendsButtonListener);
