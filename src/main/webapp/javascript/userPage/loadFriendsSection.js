async function loadFriendsHTML() {
    try {
        const response = await fetch("userDivSmall.html");
        if (!response.ok) {
            throw new Error("Failed to load friends.html: " + response.status);
        }
        const html = await response.text();
        document.getElementById("friends-container").innerHTML = html;
    } catch (error) {
        console.error(error);
    }
}

async function loadFriendsSection(username) {
    try {
        console.log(username);
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

        // Show it if it's hidden
        peopleDiv.style.display = "block";

        // Clear previous content
        peopleDiv.innerHTML = "";

        // Add a heading before the list
        const heading = document.createElement("h4");
        heading.textContent = "FRIENDS:";
        peopleDiv.appendChild(heading);

        // Add a <div> for each friend
        friends.forEach(friend => {
            const div = document.createElement("div");
            div.className = "friend-box";
            console.log(friend);
            // Create a link to the friend's account
            const link = document.createElement("a");
            link.href = `/profile?username=${encodeURIComponent(friend)}`;
            link.textContent = friend;
            link.className = "friend-link";

            div.appendChild(link);
            peopleDiv.appendChild(div);
        });

    } catch (error) {
        console.error("Error loading friends:", error);
    }
}


async function setupFriendsButtonListener() {
    await loadFriendsHTML();  // <-- Load friends HTML first

    const params = new URLSearchParams(window.location.search);
    let username = params.get("username");
    console.log("username : " + username);
    const button = document.getElementById("friendsMenuItem");
    if (!button) return;

    button.addEventListener("click", async (e) => {
        e.preventDefault();
        document.getElementById("user").style.display = "none";
        document.getElementById("statistics").style.display = "none";
        document.getElementById("friends-container").style.display = "block";

        await loadFriendsSection(username);
    });
}

document.addEventListener("DOMContentLoaded", setupFriendsButtonListener);
