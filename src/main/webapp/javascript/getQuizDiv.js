import { loadSessionValue } from "./getSessionInfo.js";

export async function getQuizDiv(quiz){
    const userType = await loadSessionValue("type");
    const userid = await loadSessionValue("userid");

    const removeBtn = document.createElement("button");
    removeBtn.className = "btn btn-danger btn-sm";
    removeBtn.textContent = "Remove Quiz";
    removeBtn.addEventListener("click", async () => {
        if (!confirm("Are you sure you want to delete this quiz?")) return;

        const res = await fetch("/deleteQuiz", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({id: quiz.id})
        }).then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Quiz deleted successfully!");
                   location.reload();

                } else {
                    alert("Error: " + data.message);
                }
            })
            .catch(err => {
                console.error("Fetch error:", err);
                alert("Something went wrong.");
            });
    });


    return `<div class="col">
            <div class="card quiz-card">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <h5 class="card-title">${quiz.title}</h5>
                        <a  href="/user?userName=${quiz.creatorName}" class="badge bg-primary">${quiz.creatorName || 'Unknown'}</a>
                    </div>
                
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted d-block">Created: ${quiz.createDate}</small>
                        </div>
                        <a href="/staetQuiz?id=${quiz.id}" class="btn btn-sm btn-primary">Take Quiz</a>
                        ${userid === quiz.creatorid || userType ==="Admin" ? removeBtn : ""}
                        <button class=""
                    </div>
                </div>
            </div></div>
        `;





}

