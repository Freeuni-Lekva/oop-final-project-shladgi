document.addEventListener("DOMContentLoaded", () => {
    const tabs = document.querySelectorAll("#notificationTabs .nav-link");
    const content = document.getElementById("notificationContent");
    const seeMoreBtn = document.getElementById("seeMoreBtn");
    const seeMoreContainer = document.getElementById("seeMoreContainer");

    let currentType = "receivedNotes";
    let currentPage = 1;

    // Load notifications for a type and page
    async function loadNotifications(type, page, append = false) {
        try {
            if (!append) content.innerHTML = `<p class="text-muted">Loading...</p>`;
            const res = await fetch("/getNotifications", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ type, page })
            });

            const data = await res.json();
            if (!data.success) {
                content.innerHTML = `<div class="alert alert-danger">Something went wrong</div>`;
                seeMoreContainer.style.display = "none";
                return;
            }



            if (!append) content.innerHTML = "";
            if(data.items.length==0 && page ==1) {
                content.innerHTML =`
                    <div class="card text-center mt-4">
    <div class="card-body">
        <h5 class="card-title text-muted">No Information</h5>
        <p class="card-text">There’s nothing to show here yet.</p>
    </div>
</div>
`;

            }

            // Render notifications
            data.items.forEach(item => {
                if (item.type === "note") {
                    content.appendChild( createNoteCard(item));
                } else if (item.type === "challenge") {
                    content.appendChild(createChallengeCard(item));
                }
            });

            // Show/hide See More
            if (data.hasMore) {
                seeMoreContainer.style.display = "block";
            } else {
                seeMoreContainer.style.display = "none";
            }

        } catch (err) {
            console.error("Fetch error:", err);
            content.innerHTML = `<div class="alert alert-danger">Failed to load notifications.</div>`;
            seeMoreContainer.style.display = "none";
        }
    }

    // Handle tab click
    tabs.forEach(tab => {
        tab.addEventListener("click", (e) => {
            e.preventDefault();
            tabs.forEach(t => t.classList.remove("active"));
            tab.classList.add("active");
            currentType = tab.dataset.type;
            currentPage = 1;
            loadNotifications(currentType, currentPage);
        });
    });

    // See More button click
    seeMoreBtn.addEventListener("click", () => {
        currentPage++;
        loadNotifications(currentType, currentPage, true);
    });

    // Initial load
    loadNotifications(currentType, currentPage);
});


 function createNoteCard(noteObj) {
    const card = document.createElement("div");
    card.className = "card mb-3 shadow-sm";

    card.innerHTML = `
        <div class="card-body ${noteObj.viewed ? "":"viewed"}">
            <div class="d-flex justify-content-between">
                <h5 class="card-title mb-0">${noteObj.text}</h5>
                 <span class="badge bg-info text-dark">Note</span>
               
            </div>
            <p class="mb-1 text-muted"><small>${new Date(noteObj.createDate).toLocaleString()}</small></p>
            <p class="mb-1"> 
                <a href="/user?username=${noteObj.username}">${noteObj.username}</a>
            </p>
             <span class="badge bg-${noteObj.viewed ? "secondary" : "success"}">
                    ${noteObj.viewed ? "Viewed" : "New"}
                </span>
            
        </div>
    `;

    return card;
}


 function createChallengeCard(challengeObj) {
    const card = document.createElement("div");
    card.className = "card mb-3 shadow-sm border-primary";

    card.innerHTML = `
        <div class="card-body ${challengeObj.viewed ? "" : "viewed"}">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="card-title mb-0">${challengeObj.quizTitle}</h5>
                <span class="badge bg-info text-dark">Challenge</span>
            </div>
            <p class="mb-1 text-muted"><small>${new Date(challengeObj.createDate).toLocaleString()}</small></p>
            <p class="mb-1">
               
                <a href="/user?username=${challengeObj.username}">${challengeObj.username}</a>
            </p>
            <span class="badge bg-${challengeObj.viewed ? "secondary" : "success"}">
                    ${challengeObj.viewed ? "Viewed" : "New"}
                </span>
            <p class="mb-0">
                <strong>Best Score:</strong> ${challengeObj.score}
            </p>
            <a href="/startQuiz?id=${challengeObj.quizId}" class="btn btn-sm btn-outline-primary mt-2">Take Challenge</a>
        </div>
    `;

    return card;
}



