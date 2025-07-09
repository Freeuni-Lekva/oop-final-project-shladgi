

export function deleteQuiz(quizId){
    fetch("/deleteQuiz", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `id=${encodeURIComponent(quizId)}`
    }).catch(error => {
            console.error("Network error while deleting questions:", error);
        });
}