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
    row.className = "border rounded p-3 mb-3";

    if(wantTitle){
        row.innerHTML = `
        <p><strong>Title:</strong> ${data.title}</p>
        <p><strong>Score:</strong> ${data.totalscore}</p>
        <p><strong>Time Taken:</strong> ${data.timetaken}</p>
        <p><strong>Date:</strong> ${data.creationdate}</p>
        <a href="/quizResult?id=${data.quizResultId}" class="btn btn-primary">View Details</a>
    `;
    } else {
        row.innerHTML = `
        <p><strong>Score:</strong> ${data.totalscore}</p>
        <p><strong>Time Taken:</strong> ${data.timetaken}</p>
        <p><strong>Date:</strong> ${data.creationdate}</p>
        <a href="/quizResult?id=${data.quizResultId}" class="btn btn-primary">View Details</a>
    `;
    }

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