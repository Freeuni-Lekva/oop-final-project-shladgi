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
    row.className = "border rounded p-2 mb-2 shadow-sm bg-light small"; // smaller padding + font size

    let infoItems = '';

    if (wantTitle) {
        infoItems += `<div class="me-3"><strong>Title:</strong> ${data.title}</div>`;
    }

    infoItems += `
        <div class="me-3"><strong>Score:</strong> ${data.totalscore}</div>
        <div class="me-3"><strong>Time:</strong> ${data.timetaken}</div>
        <div class="me-3"><strong>Date:</strong> ${data.creationdate}</div>
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