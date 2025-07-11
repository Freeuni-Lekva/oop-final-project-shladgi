document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetch("/recent-activities", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const activities = await response.json();

        const container = document.getElementById("most-recent-activities-container");

        if (container) {
            container.innerHTML = ""; // clear before adding new content

            const heading = document.createElement("h2");
            heading.textContent = "Recent Activities:";
            container.appendChild(heading);

            if (activities === "notInAcc" || activities.length === 0) {
                const p = document.createElement("p");
                p.textContent = "No recent activities found.";
                container.appendChild(p);
                return;
            }

            activities.forEach(act => {
                const div = document.createElement("div");
                div.className = "activity mb-3 p-2 border rounded";

                let content = `<p><strong>${act.friendUsername}</strong> `;

                if (act.type === "quiz_created") {
                    // Remove prefix if present and only keep the title
                    const cleanTitle = act.title.replace(/^Created quiz:\s*/i, '');
                    content += `created quiz: <a href="/startQuiz?id=${encodeURIComponent(act.id)}" class="text-decoration-none">${cleanTitle}</a>`;
                } else if (act.type === "achievement_earned") {
                    content += `earned an achievement.`;
                } else if (act.type === "quiz_result_earned") {
                    content += `completed a quiz with score: ${act.totalScore} and time: ${act.timeTaken} sec`;
                } else {
                    content += `did something.`;
                }

                content += `</p>`;
                content += `<small class="text-muted">At: ${new Date(act.creationTime).toLocaleString()}</small>`;

                div.innerHTML = content;
                container.appendChild(div);
            });
        }


    } catch (error) {
        console.error("Failed to load recent activities:", error);
    }
});
