import { getQuizDiv } from "../getQuizDiv.js";

document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("popular-quizzes-container");

    try {
        const response = await fetch("/add-popular-quizzes", {
            method: "POST"
            // no need for headers/content-type if no body
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const quizzes = await response.json();

        container.innerHTML = "";

        // Add heading first
        const heading = document.createElement("h2");
        heading.textContent = "Top Rated Quizzes:";
        container.appendChild(heading);

        if (quizzes.length === 0) {
            const p = document.createElement("p");
            p.textContent = "No recent quizzes found.";
            container.appendChild(p);
            return;
        }

        const row = document.createElement("div");
        row.className = "row g-3";

        for (const quiz of quizzes) {
            const col = document.createElement("div");
            col.className = "col-md-4";

            const quizDiv = await getQuizDiv(quiz);

            col.appendChild(quizDiv);
            row.appendChild(col);
        }

        container.appendChild(row);

    } catch (error) {
        container.innerHTML = `<p class="text-danger">Failed to load popular quizzes: ${error.message}</p>`;
        console.error("Error loading popular quizzes:", error);
    }
});
