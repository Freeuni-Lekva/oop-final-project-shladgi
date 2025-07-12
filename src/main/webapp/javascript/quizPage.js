import { getTopPerformers } from './getTopPerformers.js';
import { getUserQuizResultsDiv } from './userQuizResultsDiv.js';

if (!document.getElementById("rendered-list-style")) {
    const style = document.createElement("style");
    style.id = "rendered-list-style";
    style.textContent = `
    .rendered-list-container {
        margin-top: 1.5rem;
        background-color: #ffffff;
        border-radius: 8px;
        padding: 1rem 1.5rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .rendered-list {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .rendered-list li {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
        padding: 0.6rem 0;
        border-bottom: 1px solid #e0e0e0;
        font-size: 0.95rem;
        color: #333;
    }

    .rendered-list li:last-child {
        border-bottom: none;
    }

    .rendered-list li .user {
        flex: 1;
        color: #2d3b8b;
    }

    .rendered-list li .score,
    .rendered-list li .time,
    .rendered-list li .date {
        margin-left: 1rem;
        font-size: 0.9rem;
        color: #555;
        white-space: nowrap;
    }

    .rendered-list-container .error {
        color: #d9534f;
        font-weight: 500;
        text-align: center;
        padding: 0.5rem;
    }
  `;
    document.head.appendChild(style);
}


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
                getTopPerformers(quizId, null, 5).then(div => {
                    document.getElementById("topPerformersList").parentNode.replaceChild(div, document.getElementById("topPerformersList"));
                    div.id = "topPerformersList";
                });

                getTopPerformers(quizId, 24, 5).then(div => {
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
    ul.className = "rendered-list";       // add this class for styling
    ul.innerHTML = "";

    // Optional: wrap ul with a container div with class "rendered-list-container"
    if (!ul.parentElement.classList.contains("rendered-list-container")) {
        const container = document.createElement("div");
        container.className = "rendered-list-container";
        ul.parentNode.insertBefore(container, ul);
        container.appendChild(ul);
    }

    let n = 0;
    for (let item of data) {
        n++;
        if(n === 5) break;
        let li = document.createElement("li");

        if (detailed) {
            // If you want finer styling, add spans with classes here
            li.innerHTML = `
              <span class="user">User ${item.userId ?? "You"}</span> - 
              <span class="score">Score: ${item.score}</span>, 
              <span class="time">Time: ${item.timeTaken}s</span>, 
              <span class="date">Date: ${item.date}</span>
            `;
        } else {
            li.textContent = `User ${item.userId} - Score: ${item.score}`;
        }
        ul.appendChild(li);
    }
}
