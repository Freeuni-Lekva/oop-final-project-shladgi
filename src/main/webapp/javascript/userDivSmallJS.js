import { loadSessionValue } from './getSessionInfo.js';
import { loadRequests } from './userPage/loadFriendRequests.js';
import { loadFriendsSection } from './userPage/loadFriendsSection.js';

function addAdminButton(admin, senderUsername, btnGroup) {
    if (admin === "Admin") {
        const adminBtn = document.createElement("button");
        adminBtn.className = "btn btn-warning btn-sm";
        adminBtn.textContent = "Remove User";
        adminBtn.addEventListener("click", async () => {
            try {
                const res1 = await fetch("/get-quiz-ids", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `username=${encodeURIComponent(senderUsername)}`
                });

                if (!res1.ok) throw new Error("Failed to remove user");

                const ids = await res1.json();

                for(const id of ids){
                    const res2 = await fetch("/deleteQuiz", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: `id=${encodeURIComponent(id)}`
                    });
                    if (!res2.ok) throw new Error("Failed to delete quiz");
                }

                const res3 = await fetch("/remove-user", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `username=${encodeURIComponent(senderUsername)}`
                });

                if(!res3.ok) throw new Error("Failed");
                location.reload();

            } catch (err) {
                alert(err.message);
            }
        });
        btnGroup.appendChild(adminBtn);
    }
}


export async function getUserDiv(senderUsername) {
    // Create main div container
    const div = document.createElement("div");
    div.className = "user-div alert alert-info d-flex align-items-center justify-content-between mb-2 p-2";

    const userLink = document.createElement("a");
    userLink.href = `/user.html?username=${encodeURIComponent(senderUsername)}`;
    userLink.textContent = senderUsername;
    userLink.classList.add("fw-bold", "text-decoration-none", "text-dark");
    div.appendChild(userLink);

    // Button group
    const btnGroup = document.createElement("div");
    btnGroup.classList.add("d-flex", "gap-2");

    const response = await fetch("/check", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `target=${encodeURIComponent(senderUsername)}`
    });

    if (!response.ok) {
        throw new Error("Failed to fetch friends: " + response.status);
    }

    const status = await response.json();
    const admin = await loadSessionValue("type");

    if (status === "friends") {
        // Remove Friend button
        const removeBtn = document.createElement("button");
        removeBtn.className = "btn btn-danger btn-sm";
        removeBtn.textContent = "Remove Friend";
        removeBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-remove", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(senderUsername)}`
                });
                if (res.ok) await loadFriendsSection(senderUsername);
                else alert("Failed to remove friend");
            } catch {
                alert("Error removing friend");
            }
        });
        btnGroup.appendChild(removeBtn);
        addAdminButton(admin, senderUsername, btnGroup);

    } else if (status === "requested") {
        // Accept button
        const acceptBtn = document.createElement("button");
        acceptBtn.className = "btn btn-success btn-sm";
        acceptBtn.textContent = "Accept";
        acceptBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-request/accept", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(senderUsername)}`
                });
                if (res.ok)await loadRequests();
                else alert("Failed to accept friend request");
            } catch {
                alert("Error accepting friend request");
            }
        });
        btnGroup.appendChild(acceptBtn);

        // Reject button
        const rejectBtn = document.createElement("button");
        rejectBtn.className = "btn btn-danger btn-sm";
        rejectBtn.textContent = "Reject";
        rejectBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-request/reject", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(senderUsername)}`
                });
                if (res.ok) await loadRequests();
                else alert("Failed to reject friend request");
            } catch {
                alert("Error rejecting friend request");
            }
        });
        btnGroup.appendChild(rejectBtn);
        addAdminButton(admin, senderUsername, btnGroup);
    } else if (status === "request") {
        // Remove Request button
        const removeRequestBtn = document.createElement("button");
        removeRequestBtn.className = "btn btn-warning btn-sm";
        removeRequestBtn.textContent = "Remove Request";
        removeRequestBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/sent-request", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(senderUsername)}`
                });
                if (res.ok) location.reload();
                else alert("Failed to remove friend request");
            } catch {
                alert("Error removing friend request");
            }
        });
        btnGroup.appendChild(removeRequestBtn);
        addAdminButton(admin, senderUsername, btnGroup);

    } else {
        // Send Friend Request button
        const sendReqBtn = document.createElement("button");
        sendReqBtn.className = "btn btn-primary btn-sm";
        sendReqBtn.textContent = "Send Friend Request";
        sendReqBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-request/send", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(senderUsername)}`
                });
                if (res.ok) location.reload();
                else alert("Failed to send friend request");
            } catch {
                alert("Error sending friend request");
            }
        });
        btnGroup.appendChild(sendReqBtn);
        addAdminButton(admin, senderUsername, btnGroup);
    }

    div.appendChild(btnGroup);
    return div;
}
