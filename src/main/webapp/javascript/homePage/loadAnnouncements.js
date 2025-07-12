import { loadSessionValue } from "../getSessionInfo.js";
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
        const renderNextChunk = async () => {
            const nextChunk = announcements.slice(displayedCount, displayedCount + pageSize);
            const userType = await loadSessionValue("type");

            nextChunk.forEach(ann => {
                const card = document.createElement("div");
                card.className = "announcement-card mb-4 p-4 bg-white rounded shadow-sm position-relative";

                // Title
                const title = document.createElement("h4");
                title.className = "mb-2 fw-bold";
                title.textContent = ann.title;

                // Content
                const content = document.createElement("p");
                content.className = "mb-3 text-secondary";
                content.textContent = ann.content;

                // Image (if any)
                let image;
                if (ann.image) {
                    image = document.createElement("img");
                    image.src = ann.image;
                    image.alt = "Announcement Image";
                    image.className = "announcement-image mb-3 rounded";
                    image.style.maxWidth = "100%";
                    image.style.objectFit = "cover";
                }

                // Footer: author + date
                const footer = document.createElement("div");
                footer.className = "d-flex justify-content-between text-muted fst-italic small";
                footer.innerHTML = `
            <span>By: ${ann.author}</span>
            <span>${new Date(ann.date).toLocaleString()}</span>
        `;

                card.appendChild(title);
                card.appendChild(content);
                if (image) card.appendChild(image);
                card.appendChild(footer);

                // Admin remove button top-right corner
                if (userType === "Admin") {
                    const removeBtn = document.createElement("button");
                    removeBtn.className = "btn btn-sm btn-outline-danger position-absolute top-2 end-2";
                    removeBtn.title = "Remove Announcement";
                    removeBtn.innerHTML = `<i class="bi bi-trash"></i>`;

                    removeBtn.addEventListener("click", () => {
                        if (!confirm("Are you sure you want to delete this announcement?")) return;

                        fetch(`/announcements?id=${ann.id}`, { method: "DELETE" })
                            .then(res => res.json())
                            .then(data => {
                                if (data.success) {
                                    alert("Announcement deleted successfully.");
                                    location.reload();
                                } else {
                                    alert(data.message);
                                }
                            })
                            .catch(err => {
                                alert("Failed to delete announcement. Server error.");
                                console.error(err);
                            });
                    });

                    card.appendChild(removeBtn);
                }

                container.appendChild(card);
            });

            displayedCount += nextChunk.length;

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
