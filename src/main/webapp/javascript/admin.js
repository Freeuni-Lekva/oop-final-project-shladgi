document.addEventListener("DOMContentLoaded", ()=>{




document.getElementById("announcementForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    console.log(6454);

    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();
    const image = document.getElementById("image").value.trim();

    const res = await fetch("/announcements", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
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

})