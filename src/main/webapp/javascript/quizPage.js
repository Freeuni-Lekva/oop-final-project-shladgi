document.addEventListener("DOMContentLoaded", function () {
    const titleEl = document.getElementById("quizTitle");
    const totalQuestionsEl = document.getElementById("totalQuestions");
    const totalScoreEl = document.getElementById("totalScore");
    const practiceModeSection = document.getElementById("practiceModeSection");
    const statusDiv = document.getElementById("quizStatus");

    const quizId = new URLSearchParams(window.location.search).get("id");

    if (!quizId) {
        titleEl.textContent = "❌ Quiz ID is missing.";
        return;
    }

    // Load quiz info
    fetch("quizInfo?id=" + quizId)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                titleEl.textContent = data.title;
                totalQuestionsEl.textContent = data.totalQuestions;
                totalScoreEl.textContent = data.totalScore;
                if (data.practiceAvailable) {
                    practiceModeSection.style.display = "block";
                } else {
                    practiceModeSection.style.display = "none";
                }
            } else {
                titleEl.textContent = "❌ " + data.message;
            }
        })
        .catch(() => {
            titleEl.textContent = "❌ Failed to load quiz.";
        });

    // Start Quiz button click handler
    document.getElementById("startQuizButton").addEventListener("click", () => {
        const practice = document.getElementById("practiceMode").checked;

        fetch("startQuiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                id: quizId,
                practice: practice
            })
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    // es merea dasaweri
                    window.location.href = "/quiz?id=" + quizId; // redirect to quiz page, adjust URL as needed
                } else {
                    statusDiv.textContent = "❌ " + data.message;
                    statusDiv.style.color = "red";
                }
            })
            .catch(() => {
                statusDiv.textContent = "❌ Server error.";
                statusDiv.style.color = "red";
            });
    });
});
