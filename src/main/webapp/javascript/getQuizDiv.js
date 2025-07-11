import { loadSessionValue } from "./getSessionInfo.js";

export async function getQuizDiv(quiz){
    const userType = await loadSessionValue("type");
    const userid = await loadSessionValue("userid");
    const userName = await loadSessionValue("username");

    let r = document.createElement("div");
    r.className = "col-sm-12 col-md-6 col-lg-4  mb-4";

    r.innerHTML = `
        <div class="card quiz-card h-100 shadow-sm">
            <div class="card-body d-flex flex-column">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h5 class="card-title mb-0 text-truncate" style="max-width: 70%;">${quiz.title}</h5>
                    <a href="/user?username=${quiz.creatorName}" class="badge bg-primary text-decoration-none">${quiz.creatorName || 'Unknown'}</a>
                </div>
                
                <p class="card-text text-muted small mb-3">
                    <i class="bi bi-calendar me-1"></i> Created: ${quiz.createDate ? new Date(quiz.createDate).toLocaleDateString() : 'Unknown date'}
                </p>
                
                <div class="mt-auto d-flex justify-content-between align-items-center">
                    <a href="/startQuiz?id=${quiz.id}" class="btn btn-primary btn-sm flex-grow-1 me-2">
                        <i class="bi bi-play-fill me-1"></i> Take Quiz
                    </a>
                    <div class="forbtn"></div>
                </div>
            </div>
        </div>
    `;

    if(userName === quiz.creatorName || userType ==="Admin"){
        const removeBtn = document.createElement("button");
        removeBtn.className = "btn btn-outline-danger btn-sm";
        removeBtn.innerHTML = '<i class="bi bi-trash"></i>';
        removeBtn.title = "Remove Quiz";
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

        r.querySelector(".forbtn").appendChild(removeBtn);
    }

    return r ;
}

