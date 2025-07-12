if (!document.getElementById("user-quiz-result-style")) {
    const style = document.createElement("style");
    style.id = "user-quiz-result-style";
    style.textContent = `
    .user-quiz-result-card {
      margin-top: 1rem;
      background-color: #ffffff;
      border-radius: 8px;
      padding: 1rem 1.5rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      font-size: 0.95rem;
      color: #333;
    }

    .user-quiz-result-card .info-item {
      margin-right: 1rem;
      margin-bottom: 0.3rem;
      font-weight: 600;
      color: #2d3b8b;
      white-space: nowrap;
    }

    .user-quiz-result-card .info-item strong {
      color: #4257B2;
      margin-right: 0.3rem;
      font-weight: 700;
    }

    .user-quiz-result-card a.btn-link {
      font-weight: 600;
      color: #0d6efd;
      text-decoration: none;
      margin-left: auto;
    }

    .user-quiz-result-card a.btn-link:hover {
      text-decoration: underline;
    }
  `;
    document.head.appendChild(style);
}



export function fetchQuizResultData(quizResultId) {
    return fetch("/quizResult", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `id=${encodeURIComponent(quizResultId)}`
    }).then(response => {
        if (!response.ok) throw new Error("Failed to load result");
        return response.json();
    });
}

export function userQuizResultsDiv(data, wantTitle = false) {
    const row = document.createElement("div");
    row.className = "user-quiz-result-card";

    let infoItems = '';

    if (wantTitle) {
        infoItems += `<div class="info-item"><strong>Title:</strong> ${data.title}</div>`;
    }

    infoItems += `
        <div class="info-item"><strong>Score:</strong> ${data.totalscore}</div>
        <div class="info-item"><strong>Time:</strong> ${data.timetaken}</div>
        <div class="info-item"><strong>Date:</strong> ${data.creationdate}</div>
    `;

    row.innerHTML = `
        <div class="d-flex flex-wrap align-items-center justify-content-between">
            <div class="d-flex flex-wrap align-items-center">
                ${infoItems}
            </div>
            <a class="btn btn-sm btn-link ms-auto mt-1 mt-md-0" href="/quizResult?id=${data.quizResultId}">
                View
            </a>
        </div>
    `;

    return row;
}





export async function getUserQuizResultsDiv(userId, quizId, currentResultId, amount = 5) {
    const listContainer = document.createElement("div");
    listContainer.className = "list-group";

    try {
        const response = await fetch("/userQuizResults", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `userid=${encodeURIComponent(userId)}&quizid=${encodeURIComponent(quizId)}`
        });

        if (!response.ok) throw new Error("Failed to load previous result IDs");
        const data = await response.json();

        const filteredIds = data.resultIds.filter(id => currentResultId == null || id !== currentResultId);

        if(filteredIds.length === 0){
            const row = document.createElement("div");
            row.className = "list-group-item";
            row.innerHTML = `<p class="mb-0"><strong>No previous attempts found</strong></p>`;
            listContainer.appendChild(row);
            return listContainer;
        }

        let counter = 0;
        for (const id of filteredIds) {
            try {
                counter++;
                if(counter > amount) break;
                const result = await fetchQuizResultData(id);
                result.quizResultId = id;
                const card = userQuizResultsDiv(result);
                card.classList.add("list-group-item");
                listContainer.appendChild(card);
            } catch (err) {
                console.error("Error loading result:", err);
            }
        }

        return listContainer;
    } catch (err) {
        console.error("Error fetching previous result IDs:", err);
        const errorDiv = document.createElement("div");
        errorDiv.className = "list-group-item text-danger";
        errorDiv.textContent = "Error loading previous attempts";
        listContainer.appendChild(errorDiv);
        return listContainer;
    }
}