document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetch("/get-announcement", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const announcements = await response.json();

        const container = document.getElementById("announcements-container"); // use correct ID from your HTML

        if (container) {
            announcements.forEach(ann => {
                const div = document.createElement("div");
                div.className = "announcement";

                div.innerHTML = `
                    <h3>${ann.title}</h3>
                    <p>${ann.content}</p>
                    <p><em>By: ${ann.author}</em></p>
                    <p><small>Created at: ${new Date(ann.date).toLocaleString()}</small></p>
                    ${ann.image ? `<img src="${ann.image}" alt="Announcement image" style="max-width: 100%;">` : ""}
                `;

                container.appendChild(div);
            });
        }

        //console.log("Announcements loaded");
    } catch (error) {
        console.error("Failed to load announcements:", error);
    }
});
