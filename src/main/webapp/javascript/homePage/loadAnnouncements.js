document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetch("/get-announcement", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const announcements = await response.json();
        const container = document.getElementById("announcements-container");

        if (!container) return;

        // Add heading first
        const heading = document.createElement("h2");
        heading.textContent = "Announcements:";
        container.appendChild(heading);

        let displayedCount = 0;
        const pageSize = 10;

        // Function to render next chunk
        const renderNextChunk = () => {
            const nextChunk = announcements.slice(displayedCount, displayedCount + pageSize);
            nextChunk.forEach(ann => {
                const div = document.createElement("div");
                div.className = "announcement mb-3 p-2 border rounded";

                div.innerHTML = `
                    <h3>${ann.title}</h3>
                    <p>${ann.content}</p>
                    <p><em>By: ${ann.author}</em></p>
                    <p><small>Created at: ${new Date(ann.date).toLocaleString()}</small></p>
                    ${ann.image ? `<img src="${ann.image}" alt="Announcement image" style="max-width: 100%;">` : ""}
                `;

                container.appendChild(div);
            });
            displayedCount += nextChunk.length;

            // Hide button if nothing more to show
            if (displayedCount >= announcements.length) {
                showMoreBtn.style.display = "none";
            }
        };

        // Show first chunk initially
        renderNextChunk();

        // Create and add Show More button
        const showMoreBtn = document.createElement("button");
        showMoreBtn.textContent = "Show More";
        showMoreBtn.className = "btn btn-primary mt-2";
        showMoreBtn.addEventListener("click", renderNextChunk);
        container.appendChild(showMoreBtn);

        //console.log("Announcements loaded");
    } catch (error) {
        console.error("Failed to load announcements:", error);
    }
});
