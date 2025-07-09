export async function getUserDiv(senderUsername) {
    const params = new URLSearchParams(window.location.search);
    const currentUser = params.get("username");
    if (!currentUser) throw new Error("Current user not found in URL");

    // Create main div container
    const div = document.createElement("div");
    div.className = "user-div alert alert-info d-flex align-items-center justify-content-between mb-2 p-2";

    // Username link (instead of plain text)
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
        body: `username=${encodeURIComponent(currentUser)}&target=${encodeURIComponent(senderUsername)}`
    });

    if (!response.ok) {
        throw new Error("Failed to fetch friends: " + response.status);
    }

    const status = await response.json();

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
                    body: `friendUsername=${encodeURIComponent(senderUsername)}&currentUser=${encodeURIComponent(currentUser)}`
                });
                if (res.ok) location.reload();
                else alert("Failed to remove friend");
            } catch {
                alert("Error removing friend");
            }
        });
        btnGroup.appendChild(removeBtn);

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
                    body: `senderUsername=${encodeURIComponent(senderUsername)}&receiverUsername=${encodeURIComponent(currentUser)}`
                });
                if (res.ok) location.reload();
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
                    body: `senderUsername=${encodeURIComponent(senderUsername)}&receiverUsername=${encodeURIComponent(currentUser)}`
                });
                if (res.ok) location.reload();
                else alert("Failed to reject friend request");
            } catch {
                alert("Error rejecting friend request");
            }
        });
        btnGroup.appendChild(rejectBtn);

    } else if (status === "request") {
        // Request Sent - disabled button
        const pendingBtn = document.createElement("button");
        pendingBtn.className = "btn btn-secondary btn-sm";
        pendingBtn.textContent = "Request Sent";
        pendingBtn.disabled = true;
        btnGroup.appendChild(pendingBtn);

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
                    body: `senderUsername=${encodeURIComponent(currentUser)}&receiverUsername=${encodeURIComponent(senderUsername)}`
                });
                if (res.ok) location.reload();
                else alert("Failed to send friend request");
            } catch {
                alert("Error sending friend request");
            }
        });
        btnGroup.appendChild(sendReqBtn);
    }

    div.appendChild(btnGroup);
    return div;
}
