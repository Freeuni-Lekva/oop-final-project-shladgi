document.addEventListener("DOMContentLoaded", ()=>{

document.getElementById("announcementForm").addEventListener("submit", createAnnounce(e));


    fetch("/admin")
        .then(res => res.json())
        .then(data => {
            if (!data.success) {
                alert("Failed to load statistics: " + data.message);
                return;
            }

            const statsTable = document.getElementById("statsTable").querySelector("tbody");
            statsTable.innerHTML = ""; // Clear existing rows

            const entries = {
                "Total Users": data.users,
                "Total Quizzes": data.quizzes,
                "Total Quiz Attempts": data.takenQuizzes,
                // ➕ Add more entries here if needed
            };

            for (const [key, value] of Object.entries(entries)) {
                const row = document.createElement("tr");
                row.innerHTML = `
        <th class="text-start">${key}</th>
        <td class="text-end fw-bold">${value}</td>
      `;
                statsTable.appendChild(row);
            }
        })
        .catch(err => {
            console.error("Error fetching stats:", err);
            alert("Something went wrong loading statistics.");
        });




})


async function createAnnounce(e) {
    e.preventDefault();


    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();
    const image = document.getElementById("image").value.trim();

    const res = await fetch("/announcements", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: new URLSearchParams({
            title,
            content,
            image
        })
    });

    const result = await res.json();

    if (result.success) {
        alert("Announcement created successfully!");
        document.getElementById("announcementForm").reset();
    } else {
        alert("Error: " + result.message);
    }
}


