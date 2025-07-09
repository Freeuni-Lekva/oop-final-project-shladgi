import { getUserDiv } from '../userDivSmallJS.js';

async function loadFriendsHTML() {
    try {
        const response = await fetch("userDivSmall.html");
        if (!response.ok) {
            throw new Error("Failed to load userDivSmall.html: " + response.status);
        }
        const html = await response.text();
        document.getElementById("friends-container").innerHTML = html;
    } catch (error) {
        console.error(error);
    }
}

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

        const peopleDiv = document.getElementById("people");
        if (!peopleDiv) return;

        peopleDiv.style.display = "block";
        peopleDiv.innerHTML = "";

        const heading = document.createElement("h4");
        heading.textContent = "FRIENDS:";
        peopleDiv.appendChild(heading);

        for (const friend of friends) {
            const friendDiv = await getUserDiv(friend);  // Use your shared component function
            peopleDiv.appendChild(friendDiv);
        }

    } catch (error) {
        console.error("Error loading friends:", error);
    }
}

async function setupFriendsButtonListener() {
    await loadFriendsHTML();

    const params = new URLSearchParams(window.location.search);
    const username = params.get("username");

    const button = document.getElementById("friendsMenuItem");
    if (!button || !username) return;

    button.addEventListener("click", async (e) => {
        e.preventDefault();
        console.log("Aqaa");
        document.getElementById("user").style.display = "none";
        document.getElementById("statistics").style.display = "none";
        document.getElementById("friend-requests-container").style.display = "none";
        document.getElementById("friends-container").style.display = "block";

        await loadFriendsSection(username);
    });
}

document.addEventListener("DOMContentLoaded", setupFriendsButtonListener);
