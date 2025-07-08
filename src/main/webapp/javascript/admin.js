document.addEventListener("DOMContentLoaded", () => {
    loadAnnouncements();
    loadStats();

    document.getElementById("createForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const text = document.getElementById("announcementText").value.trim();
        if (!text) return;

        await fetch("announcements", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ text })
        });

        document.getElementById("announcementText").value = "";
        loadAnnouncements();
    });
});

async function loadAnnouncements() {
    const res = await fetch("announcements");
    const data = await res.json();

    const list = document.getElementById("announcementList");
    list.innerHTML = "";

    data.forEach(item => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";
        li.innerHTML = `
      <span>${item.text}</span>
      <button class="btn btn-danger btn-sm" onclick="deleteAnnouncement(${item.id})">Delete</button>
    `;
        list.appendChild(li);
    });
}

async function deleteAnnouncement(id) {
    await fetch(`announcements?id=${id}`, { method: "DELETE" });
    loadAnnouncements();
}

async function loadStats() {
    const res = await fetch("stats");
    const stats = await res.json();
    document.getElementById("statUsers").innerText = stats.users;
    document.getElementById("statVisits").innerText = stats.visits;
    document.getElementById("statSessions").innerText = stats.activeSessions;
}
