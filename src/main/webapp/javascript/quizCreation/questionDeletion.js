

export function deleteQuestions(quizId){
    fetch("/deleteQuestions", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `quizid=${encodeURIComponent(quizId)}`
    }).catch(error => {
            console.error("Network error while deleting questions:", error);
        });
}