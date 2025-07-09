import { loadSessionValue } from "../getSessionInfo.js";

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    let viewedUsername = params.get("username");

    const sessionUsername = await loadSessionValue("username");

    const menuItem = document.getElementById("userMenuItem");
    if (menuItem && viewedUsername) {
        menuItem.textContent = viewedUsername;
    }

    if (viewedUsername !== sessionUsername) {
        const friendMenu = document.getElementById("friendRequestMenuItem");
        if (friendMenu) friendMenu.remove();
    }

    const userSection = document.getElementById("user");
    const friendsContainer = document.getElementById("friends-container");
    const statisticsSection = document.getElementById("statistics");
    const friendRequest = document.getElementById("friend-requests-container");

    if (userSection && viewedUsername) {
        userSection.style.display = "block";

        const oldUsernameDisplay = userSection.querySelector(".username-display");
        if (oldUsernameDisplay) oldUsernameDisplay.remove();

        const usernameDisplay = document.createElement("p");
        usernameDisplay.textContent = `Username: ${viewedUsername}`;
        usernameDisplay.classList.add("username-display");
        userSection.appendChild(usernameDisplay);

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

            const oldAchTitle = userSection.querySelector("h4");
            if (oldAchTitle) oldAchTitle.remove();
            const oldAchList = userSection.querySelector("ul");
            if (oldAchList) oldAchList.remove();
            const oldNoAch = userSection.querySelector(".no-achievements");
            if (oldNoAch) oldNoAch.remove();

            if (achievements.length > 0) {
                const achTitle = document.createElement("h4");
                achTitle.textContent = "Achievements:";
                userSection.appendChild(achTitle);

                const achList = document.createElement("ul");
                for (const achievement of achievements) {
                    const li = document.createElement("li");
                    li.textContent = `${achievement.title} - ${achievement.description}`;
                    achList.appendChild(li);
                }
                userSection.appendChild(achList);
            } else {
                const noAch = document.createElement("p");
                noAch.textContent = "No achievements yet.";
                noAch.classList.add("no-achievements");
                userSection.appendChild(noAch);
            }
        } catch (error) {
            console.error("Error loading achievements:", error);
        }
    }
    friendsContainer.style.display = "none";
    statisticsSection.style.display = "none";
    friendRequest.style.display = "none";
});

