import { loadSessionValue } from "../getSessionInfo.js";

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    let viewedUsername = params.get("username");

    // Load the session username
    const sessionUsername = await loadSessionValue("username");

    // Set session username on the menu
    const menuItem = document.getElementById("userMenuItem");
    if (menuItem) {
        menuItem.textContent = viewedUsername;
    }

    // Show/hide buttons based on profile ownership
    if (viewedUsername !== sessionUsername) {
        const friendMenu = document.getElementById("friendRequestMenuItem");
        if (friendMenu) friendMenu.remove();
    }

    // Show user section by default
    const userSection = document.getElementById("user");
    const friendsContainer = document.getElementById("friends-container");
    const statisticsSection = document.getElementById("statistics");

    if (userSection) {
        userSection.style.display = "block";

        // Display the username
        const usernameDisplay = document.createElement("p");
        usernameDisplay.textContent = `Username: ${viewedUsername}`;
        userSection.appendChild(usernameDisplay);

        // Fetch and display achievements
        try {
            const response = await fetch("/user", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: `username=${encodeURIComponent(viewedUsername)}`
            });

            if (!response.ok) throw new Error("Failed to fetch achievements");

            const achievements = await response.json();

            if (achievements.length > 0) {
                const achTitle = document.createElement("h4");
                achTitle.textContent = "Achievements:";
                userSection.appendChild(achTitle);

                const achList = document.createElement("ul");
                for (const achievement of achievements) {
                    const li = document.createElement("li");
                    li.textContent = `${achievement.name} - ${achievement.description}`;
                    achList.appendChild(li);
                }
                userSection.appendChild(achList);
            } else {
                const noAch = document.createElement("p");
                noAch.textContent = "No achievements yet.";
                userSection.appendChild(noAch);
            }
        } catch (error) {
            console.error("Error loading achievements:", error);
        }
    }

    if (friendsContainer) friendsContainer.style.display = "none";
    if (statisticsSection) statisticsSection.style.display = "none";
});
