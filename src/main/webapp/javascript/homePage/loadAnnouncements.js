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
                const div = document.createElement("div");
                div.className = "announcement mb-3 p-2 border rounded";

                div.innerHTML = `
                    <h3>${ann.title}</h3>
                    <p>${ann.content}</p>
                    <p><em>By: ${ann.author}</em></p>
                    <p><small>Created at: ${new Date(ann.date).toLocaleString()}</small></p>
                    ${ann.image ? `<img src="${ann.image}" alt="Announcement image" style="max-width: 100%;">` : ""}
                `;
                if(userType != null&&userType === "Admin"){
                    const removeBtn = document.createElement("button");
                    removeBtn.className = "btn btn-outline-danger btn-sm";
                    removeBtn.innerHTML = '<i class="bi bi-trash"></i>';
                    removeBtn.title = "Remove Announcement";
                    removeBtn.textContent = "Remove Announcement";
                    removeBtn.addEventListener("click",()=>{
                            if (!confirm("Are you sure you want to delete this announcement?")) return;

                            fetch( `/announcements?id=${ann.id}`, {
                                method: "DELETE",
                            })
                            .then(res => res.json())
                            .then(data => {
                                if (data.success) {
                                    alert("Announcement deleted successfully.");
                                  location.reload();

                                } else {
                                    alert(data.message );
                                }
                            })
                            .catch(err => {
                                alert("Failed to delete announcement. Server error.");
                                console.error(err);
                            });



                    });
                    div.appendChild(removeBtn);

                }

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
