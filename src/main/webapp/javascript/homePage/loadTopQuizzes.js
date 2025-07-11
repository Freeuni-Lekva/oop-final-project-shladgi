document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("popular-quizzes-container");

    try {
        const response = await fetch("/add-popular-quizzes", {
            method: "POST"
            // no need for headers/content-type if no body
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const quizzes = await response.json();

        if (quizzes.length === 0) {
            container.innerHTML = "<p>No recent quizzes found.</p>";
            return;
        }

        container.innerHTML = ""; // clear before adding new content

        const row = document.createElement("div");
        row.className = "row g-3";

        quizzes.forEach((quiz) => {
            const col = document.createElement("div");
            col.className = "col-md-4";

            const card = document.createElement("div");
            card.className = "card h-100";

            const cardBody = document.createElement("div");
            cardBody.className = "card-body";

            // Title as link
            const title = document.createElement("h5");
            title.className = "card-title";

            const link = document.createElement("a");
            link.href = `/quiz?id=${quiz.id}`; // adjust path if needed
            link.textContent = quiz.title;
            link.className = "text-decoration-none link-dark"; // bootstrap classes

            title.appendChild(link);

            // Additional info (e.g., creation date)
            const info = document.createElement("p");
            info.className = "card-text text-muted small";
            info.textContent = `Created: ${quiz.creationTime}`;

            cardBody.appendChild(title);
            cardBody.appendChild(info);
            card.appendChild(cardBody);
            col.appendChild(card);
            row.appendChild(col);
        });

        container.appendChild(row);

    } catch (error) {
        container.innerHTML = `<p class="text-danger">Failed to load recent quizzes: ${error.message}</p>`;
        console.error("Error loading recent quizzes:", error);
    }
});
