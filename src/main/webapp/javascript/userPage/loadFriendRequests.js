import { getUserDiv } from '../userDivSmallJS.js';

export async function loadRequests() {
    const reqBtn = document.getElementById("friendRequestMenuItem");

    document.getElementById("userMenuItem").classList.remove("active");
    document.getElementById("statisticsMenuItem").classList.remove("active");
    if(reqBtn)reqBtn.classList.add("active");
    document.getElementById("friendsMenuItem").classList.remove("active");


    const params = new URLSearchParams(window.location.search);
    const username = params.get("username");

    if (!username) {
        console.error("Username not found in URL.");
        return;
    }

    // Hide other sections
    document.getElementById("user").style.display = "none";
    document.getElementById("statistics").style.display = "none";
    document.getElementById("friends-container").style.display = "none";

    const container = document.getElementById("friend-requests-container");
    container.style.display = "block";
    container.innerHTML = "<p>Loading...</p>";

    try {
        // Load userDivSmall.html
        const htmlResponse = await fetch("userDivSmall.html");
        if (!htmlResponse.ok) {
            throw new Error(`Failed to load userDivSmall.html: ${htmlResponse.status}`);
        }
        const htmlContent = await htmlResponse.text();
        container.innerHTML = htmlContent;

        const peopleDiv = container.querySelector("#people");
        if (!peopleDiv) {
            throw new Error("#people div not found in loaded HTML");
        }

        peopleDiv.style.display = "block";
        peopleDiv.innerHTML = "<p>Loading requests...</p>";

        // Fetch usernames who sent friend requests
        const response = await fetch("/user-friend-request", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `username=${encodeURIComponent(username)}`
        });
        if (!response.ok) {
            throw new Error(`Failed to fetch friend requests: ${response.status}`);
        }

        const usernames = await response.json();
        peopleDiv.innerHTML = "";

        if (!Array.isArray(usernames) || usernames.length === 0) {
            peopleDiv.textContent = "No friend requests.";
            return;
        }

        // Dynamically load div for each sender using getUserDiv
        for (const senderUsername of usernames) {
            const userDiv = await getUserDiv(senderUsername);
            peopleDiv.appendChild(userDiv);
        }

    } catch (error) {
        console.error("Error loading requests:", error);
        container.innerHTML = "<p class='text-danger'>Failed to load friend requests.</p>";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const friendRequestsBtn = document.getElementById("friendRequestMenuItem");

    if (!friendRequestsBtn) {
        console.error("Friend Requests button not found.");
        return;
    }

    friendRequestsBtn.addEventListener("click", loadRequests);
});
