import { getTopPerformers } from './getTopPerformers.js';
import { getUserQuizResultsDiv } from './userQuizResultsDiv.js';

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
                creatorLink.href = "/user?username=" + data.creatorName;
                creatorLink.textContent = data.creatorName;

                // get my past results.
                getUserQuizResultsDiv(data.userId, quizId, null).then(userResultsDiv => {
                    const container = document.getElementById("userAttemptsList");
                    container.innerHTML = "";
                    container.appendChild(userResultsDiv);
                });

                // Use the new getTopPerformers function
                getTopPerformers(quizId, null, 10).then(div => {
                    document.getElementById("topPerformersList").parentNode.replaceChild(div, document.getElementById("topPerformersList"));
                    div.id = "topPerformersList";
                });

                getTopPerformers(quizId, 24, 10).then(div => {
                    document.getElementById("recentTopPerformersList").parentNode.replaceChild(div, document.getElementById("recentTopPerformersList"));
                    div.id = "recentTopPerformersList";
                });

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

                // Handle quiz buttons state
                updateQuizButtons(data.ongoingResult, data.ongoingPractice, quizId);

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
                                window.location.href = "/";
                            } else {
                                alert("Error: " + data.message);
                            }
                        })
                        .catch(() => {
                            alert("❌ Server error while trying to delete quiz.");
                        });
                });


                document.getElementById("deleteHistory").addEventListener("click", () => {
                    if (!confirm("Are you sure you want to delete this quiz's History? This action cannot be undone.")) return;

                    fetch("deleteQuiz", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: new URLSearchParams({
                            id: quizId,
                            s : "save"
                        })
                    })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                alert("Quiz's History deleted successfully!");
                                window.location.href = "/";
                            } else {
                                alert("Error: " + data.message);
                            }
                        })
                        .catch(() => {
                            alert("❌ Server error while trying to delete quiz's history.");
                        });
                });



            } else {
                titleEl.textContent = "❌ " + data.message;
            }
        })
        .catch(() => {
            titleEl.textContent = "❌ Failed to load quiz.";
        });

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
                    window.location.href = `/quiz?id=${encodeURIComponent(quizId)}&practice=${encodeURIComponent(practice)}`;
                } else {
                    // Check if this is an unfinished quiz error
                    if (data.hasUnfinished) {
                        // Create a div with the HTML message
                        const messageDiv = document.createElement("div");
                        messageDiv.innerHTML = data.message;
                        messageDiv.className = "alert alert-warning";
                        statusDiv.innerHTML = "";
                        statusDiv.appendChild(messageDiv);
                    } else {
                        // Regular error message
                        statusDiv.textContent = "❌ " + data.message;
                        statusDiv.style.color = "red";
                    }
                }
            })
            .catch(() => {
                statusDiv.textContent = "❌ Server error.";
                statusDiv.style.color = "red";
            });
    });

    // Cancel Quiz button click handler
    document.getElementById("cancelQuizButton")?.addEventListener("click", () => {
        if (!confirm("Are you sure you want to cancel this quiz? Your progress will be lost.")) return;

        fetch("cancelQuiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                quizId: quizId
            })
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    updateQuizButtons(false);
                } else {
                    alert("Cancel failed: " + data.message);
                }
            })
            .catch(() => {
                alert("❌ Server error while canceling quiz.");
            });
    });

    // Continue Quiz button click handler
    document.getElementById("continueQuizButton")?.addEventListener("click", () => {
        const practice = document.getElementById("practiceMode").checked;
        window.location.href = `/quiz?id=${encodeURIComponent(quizId)}&practice=${encodeURIComponent(practice)}&continue=true`;
    });
});

function updateQuizButtons(hasOngoingQuiz, isPractice = false, quizId = null) {
    if (hasOngoingQuiz) {
        document.getElementById("cancelQuizButton").style.display = "inline-block";
        document.getElementById("continueQuizButton").style.display = "inline-block";
        document.getElementById("startQuizButton").style.display = "none";
    } else {
        document.getElementById("cancelQuizButton").style.display = "none";
        document.getElementById("continueQuizButton").style.display = "none";
        document.getElementById("startQuizButton").style.display = "inline-block";
    }
}

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