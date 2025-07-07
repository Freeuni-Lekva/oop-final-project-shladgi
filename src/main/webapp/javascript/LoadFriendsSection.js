async function loadFriendsSection() {
    try {
        const response = await fetch("/friends");
        if (!response.ok) throw new Error("Failed to fetch friends");

        const friends = await response.json();

        const friendsDiv = document.getElementById("friends");
        const ul = friendsDiv.querySelector("ul");
        ul.innerHTML = "";

        friends.forEach(friend => {
            const li = document.createElement("li");
            li.textContent = friend;
            ul.appendChild(li);
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

        document.getElementById("user").style.display = "none";
        document.getElementById("rating").style.display = "none";
        document.getElementById("friends").style.display = "block";

        await loadFriendsSection();
    });
}

document.addEventListener("DOMContentLoaded", setupFriendsButtonListener);
