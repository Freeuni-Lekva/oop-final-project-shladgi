document.addEventListener("DOMContentLoaded", ()=>{
    loadStats("all");
    document.getElementById("announcementForm").addEventListener("submit", async (e) => {
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


    });
    document.getElementById("period").addEventListener("change", (e) => {
        loadStats(e.target.value);
    });
});




async function loadStats(period = "all") {
    try {
        const res = await fetch("/admin", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ period })

        });


        const data = await res.json();
        if (!data.success) {
            alert("Failed to load statistics: " + data.message);
           // return;
        }
        console.log(55)

        const statsTable = document.getElementById("statsTable").querySelector("tbody");
        statsTable.innerHTML = "";

        const entries = {
            "Total Users": data.totalusers,
            "Total Quizzes": data.totalquizzes,
            "Total Quiz Attempts": data.totaltakenquizzes,
            "New Users": data.newusers,
            "New Quizzes": data.newquizzes,
            "Recently taken Quizzes": data.newtakenquizes

        };



        for (const [key, value] of Object.entries(entries)) {
            const row = document.createElement("tr");
            row.innerHTML = `
        <th class="text-start">${key}</th>
        <td class="text-end fw-bold">${value}</td>
      `;
            statsTable.appendChild(row);
        }

    } catch (err) {
        console.error("Error:", err);
        alert("Failed to load stats.");
    }
}






