import { loadSessionValue } from './getSessionInfo.js';

function addAdminButton(admin, receiverUsername, btnGroup, div, receiverType = "Basic") {
    if (admin === "Admin") {
        const adminBtn = document.createElement("button");
        adminBtn.className = "btn btn-warning btn-sm";
        adminBtn.textContent = "Remove User";
        adminBtn.addEventListener("click", async () => {
            try {
                if (!confirm("Are you sure you want to delete this user?")) return;
                const res1 = await fetch("/get-quiz-ids", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `username=${encodeURIComponent(receiverUsername)}`
                });

                if (!res1.ok) throw new Error("Failed to get quizzes");

                const ids = await res1.json();

                for (const id of ids) {
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
                    body: `username=${encodeURIComponent(receiverUsername)}`
                });

                if (!res3.ok) throw new Error("Failed to remove user");

                div.remove();  // ✅ remove div from DOM
            } catch (err) {
                alert(err.message);
            }
        });

        btnGroup.appendChild(adminBtn);


        fetch("/getUserType",{
            method: 'POST',
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `username=${encodeURIComponent(receiverUsername)}`
        }).then(res => {
            if(res.ok)return res.json()
            else console.log(res.message)

        }).then(data => {

        if(data.type !=="Admin") {
            const promote = document.createElement("button");
            promote.className = "btn btn-primary btn-sm";
            promote.textContent = "Promote User";
            promote.addEventListener("click", () => {
                if (!confirm("Are you sure you want to promote this user?")) return;
                fetch("/promoteUser", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams({
                        username: receiverUsername,
                    })
                }).then(res => res.json())
                    .then(data => {
                        if (data.success){
                            alert(`You successfully promoted ${receiverUsername} to Admin.`);
                            location.reload()
                        }
                        else {
                            console.log(77);
                            alert(data.message)
                        }
                    }).catch(err => console.log(err))
            });

            btnGroup.appendChild(promote);
        }


}).catch(err=> console.log(err));
}}

export async function updateButtons(status, btnGroup, receiverUsername, admin, div, receiverType = "Basic") {
    btnGroup.innerHTML = ""; // clear buttons

    if (status === "friends") {
        const removeBtn = document.createElement("button");
        removeBtn.className = "btn btn-danger btn-sm";
        removeBtn.textContent = "Remove Friend";
        removeBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-remove", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(receiverUsername)}`
                });
                if (res.ok) {
                    await updateButtons("default", btnGroup, receiverUsername, admin, div,receiverType);
                } else {
                    alert("Failed to remove friend");
                }
            } catch {
                alert("Error removing friend");
            }
        });
        btnGroup.appendChild(removeBtn);
    }

    else if (status === "requested") {
        const acceptBtn = document.createElement("button");
        acceptBtn.className = "btn btn-success btn-sm";
        acceptBtn.textContent = "Accept";
        acceptBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-request/accept", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(receiverUsername)}`
                });
                if (res.ok) {
                    await updateButtons("friends", btnGroup, receiverUsername, admin, div,receiverType);
                } else {
                    alert("Failed to accept friend request");
                }
            } catch {
                alert("Error accepting friend request");
            }
        });
        btnGroup.appendChild(acceptBtn);

        const rejectBtn = document.createElement("button");
        rejectBtn.className = "btn btn-danger btn-sm";
        rejectBtn.textContent = "Reject";
        rejectBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-request/reject", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(receiverUsername)}`
                });
                if (res.ok) {
                    await updateButtons("default", btnGroup, receiverUsername, admin, div);
                } else {
                    alert("Failed to reject friend request");
                }
            } catch {
                alert("Error rejecting friend request");
            }
        });
        btnGroup.appendChild(rejectBtn);
    }

    else if (status === "request") {
        const removeRequestBtn = document.createElement("button");
        removeRequestBtn.className = "btn btn-warning btn-sm";
        removeRequestBtn.textContent = "Remove Request";
        removeRequestBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/sent-request", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(receiverUsername)}`
                });
                if (res.ok) {
                    await updateButtons("default", btnGroup, receiverUsername, admin, div);
                } else {
                    alert("Failed to remove friend request");
                }
            } catch {
                alert("Error removing friend request");
            }
        });
        btnGroup.appendChild(removeRequestBtn);
    }

    else { // default: no request or friendship
        const sendReqBtn = document.createElement("button");
        sendReqBtn.className = "btn btn-primary btn-sm";
        sendReqBtn.textContent = "Send Friend Request";
        sendReqBtn.addEventListener("click", async () => {
            try {
                const res = await fetch("/friend-request/send", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `target=${encodeURIComponent(receiverUsername)}`
                });
                if (res.ok) {
                    await updateButtons("request", btnGroup, receiverUsername, admin, div);
                } else {
                    alert("Failed to send friend request");
                }
            } catch {
                alert("Error sending friend request");
            }
        });
        btnGroup.appendChild(sendReqBtn);
    }

    addAdminButton(admin, receiverUsername, btnGroup, div, receiverType );
}

export async function getUserDiv(receiverUsername, receiverType = "Basic") {
    const div = document.createElement("div");
    div.className = "user-div alert alert-info d-flex align-items-center justify-content-between mb-2 p-2";

    const myName = await loadSessionValue("username");

    const userLink = document.createElement("a");
    userLink.href = `/user?username=${encodeURIComponent(receiverUsername)}`;
    userLink.textContent = receiverUsername;
    userLink.classList.add("fw-bold", "text-decoration-none", "text-dark");
    div.appendChild(userLink);

    const res = await fetch("/solved-created-quizzes", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `username=${encodeURIComponent(receiverUsername)}`
    });

    const amount = await res.json();

    const statsText = document.createElement("span");
    if(amount.solved == 1){
        statsText.textContent = ` - Solved ${amount.solved} quizz, Created ${amount.created} quizzes`;
    }else{
        statsText.textContent = ` - Solved ${amount.solved} quizzes, Created ${amount.created} quizzes`;
    }
    statsText.classList.add("ms-2", "text-muted");
    div.appendChild(statsText);


    const btnGroup = document.createElement("div");
    btnGroup.classList.add("d-flex", "gap-2");
    div.appendChild(btnGroup);

    try {
        const admin = await loadSessionValue("type");

        const response = await fetch("/check", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `target=${encodeURIComponent(receiverUsername)}`
        });

        if (!response.ok) throw new Error("Failed to fetch friend status");

        const status = await response.json();

        if(status !== "guest" &&  myName !== receiverUsername){
            await updateButtons(status, btnGroup, receiverUsername, admin, div,receiverType);
        }
    } catch (error) {
        alert(error.message);
    }
    return div;
}
