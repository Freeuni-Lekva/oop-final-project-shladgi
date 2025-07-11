import { fetchQuizResultData, userQuizResultsDiv } from "../userQuizResultsDiv.js";

document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("statisticsMenuItem");
    if (!button) {
        console.error("Statistics button not found");
        return;
    }

    button.addEventListener("click", async () => {

        const userBtn = document.getElementById("userMenuItem");
        const statBtn = document.getElementById("statisticsMenuItem");
        const frnReqBtn = document.getElementById("friendRequestMenuItem");
        const frnBtn = document.getElementById("friendsMenuItem");

        if(userBtn) userBtn.classList.remove("active");
        if(statBtn) statBtn.classList.add("active");
        if(frnReqBtn) frnReqBtn.classList.remove("active");
        if(frnBtn) frnBtn.classList.remove("active");

        document.getElementById("friend-requests-container").style.display = "none";
        document.getElementById("friends-container").style.display = "none";
        document.getElementById("user").style.display = "none";
        document.getElementById("statistics").style.display = "block";
        const statisticsSection = document.getElementById("statistics");
        statisticsSection.style.display = "block";

        try {
            const params = new URLSearchParams(window.location.search);
            const viewedUsername = params.get("username");

            const response = await fetch("/user_statistics", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: `username=${encodeURIComponent(viewedUsername)}`
            });

            if (!response.ok) throw new Error("Failed to fetch quiz result IDs");

            const ids = await response.json();

            statisticsSection.innerHTML = "";

            if (ids.length === 0) {
                statisticsSection.innerHTML = "<p>No quiz results found.</p>";
            } else {
                for (const id of ids) {
                    try {
                        const data = await fetchQuizResultData(id);
                        data.quizResultId = id;
                        const resultDiv = userQuizResultsDiv(data, true);
                        statisticsSection.appendChild(resultDiv);
                    } catch (err) {
                        console.error(`Error loading quiz result for ID ${id}:`, err);
                    }
                }
            }
        } catch (error) {
            console.error("Error loading statistics:", error);
        }
    });
});
