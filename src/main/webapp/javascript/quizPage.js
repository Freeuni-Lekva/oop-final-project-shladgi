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

                // New fields
                document.getElementById("quizDescription").textContent = data.description;
                let creatorLink = document.getElementById("creatorLink");
                creatorLink.href = "/user?id=" + data.creatorId;
                creatorLink.textContent = data.creatorName;

                renderList("userAttemptsList", data.userAttempts, true);
                renderList("topPerformersList", data.topPerformers);
                renderList("recentTopPerformersList", data.recentTopPerformers);
                renderList("recentTakersList", data.recentTakers, true);

                document.getElementById("avgScore").textContent = data.averageScore.toFixed(2);



                if (data.practiceAvailable) {
                    practiceModeSection.style.display = "block";
                } else {
                    practiceModeSection.style.display = "none";
                }

                if (data.isOwner || data.isAdmin) {
                    document.getElementById("editDeleteSection").style.display = "block";
                }

                document.getElementById("editQuizBtn").onclick = () => {
                    window.location.href = "/editQuiz?id=" + quizId;
                };

                document.getElementById("deleteQuizBtn").addEventListener("click", () => {
                    if (!confirm("Are you sure you want to delete this quiz? This action cannot be undone.")) return;

                    fetch("deleteQuiz", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: new URLSearchParams({
                            id: quizId
                        })
                    })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                alert("Quiz deleted successfully!");
                                window.location.href = "/"; // redirect to homepage or quiz list
                            } else {
                                alert("Error: " + data.message);
                            }
                        })
                        .catch(() => {
                            alert("❌ Server error while trying to delete quiz.");
                        });
                });


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


function renderList(id, data, detailed = false) {
    const ul = document.getElementById(id);
    ul.innerHTML = "";
    for (let item of data) {
        let li = document.createElement("li");
        if (detailed) {
            li.textContent = `User ${item.userId ?? "You"} - Score: ${item.score}, Time: ${item.timeTaken}s, Date: ${item.date}`;
        } else {
            li.textContent = `User ${item.userId} - Score: ${item.score}`;
        }
        ul.appendChild(li);
    }
}


