function fetchQuizResultData(quizResultId) {
    return fetch("/quizResult", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `quizResultId=${encodeURIComponent(quizResultId)}`
    }).then(response => {
        if (!response.ok) throw new Error("Failed to load result");
        return response.json();
    });
}


// After loading current result
const quizResultId = parseInt(new URLSearchParams(window.location.search).get("quizResultId"));

fetchQuizResultData(quizResultId)
    .then(async data => {
        document.getElementById("title").textContent = data.title;
        document.getElementById("totalscore").textContent = data.totalscore;
        document.getElementById("timetaken").textContent = data.timetaken;
        document.getElementById("creationdate").textContent = data.creationdate;

        // Call with current result info
        const userId = data.userid;
        const quizId = data.quizid;
        const previousResults = document.getElementById("quiz-result-list");
        const newDiv = await userQuizResultsDiv(userId, quizId, quizResultId);
        previousResults.replaceWith(newDiv);
    });


