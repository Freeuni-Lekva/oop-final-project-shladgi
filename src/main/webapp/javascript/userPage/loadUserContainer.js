import { loadSessionValue } from "../getSessionInfo.js";
import { getAchievementDiv } from "../achievementDivGetter.js";

document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("userMenuItem").classList.add("active");
    document.getElementById("statisticsMenuItem").classList.remove("active");
    document.getElementById("friendRequestMenuItem").classList.remove("active");
    document.getElementById("friendsMenuItem").classList.remove("active");

    const ids = [
        "user",
        "friends-container",
        "friend-requests-container",
        "statistics"
    ];

    const info = {};

    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            // Get computed display style
            const display = window.getComputedStyle(el).display;
            info[id] = (display !== "none");
        } else {
            info[id] = null; // if the element is missing
        }
    });
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

        // Clear old username display
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

            // Remove old elements
            userSection.querySelectorAll(".achievement-title, .achievement-container, .no-achievements")
                .forEach(el => el.remove());

            if (achievements.length > 0) {
                const achTitle = document.createElement("h4");
                achTitle.textContent = "Achievements:";
                achTitle.classList.add("achievement-title");
                userSection.appendChild(achTitle);

                const achContainer = document.createElement("div");
                achContainer.classList.add("achievement-container");
                userSection.appendChild(achContainer);

                for (const achievement of achievements) {
                    const div = await getAchievementDiv(achievement.id);
                    achContainer.appendChild(div);
                }
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
