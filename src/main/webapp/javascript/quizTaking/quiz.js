import {getQuestionWhileTaking} from "./questionWhileTaking.js";

// document.addEventListener("DOMContentLoaded", () => {
//     const params = new URLSearchParams(window.location.search);
//     const quizId = params.get("id");
//
//     fetch("/quiz", {
//         method: "POST",
//         headers: {
//             "Content-Type": "application/x-www-form-urlencoded"
//         },
//         body: `id=${encodeURIComponent(quizId)}`
//     })
//         .then(response => response.json())
//         .then(data => {
//             if (!data.success) {
//                 console.error("Error loading quiz" + data.message);
//                 return;
//             }
//
//
//             const questions = data.questions;
//
//             // Use quiz info
//             document.body.insertAdjacentHTML("afterbegin", `
//             <h2>${data.title}</h2>
//             <p>${data.description}</p>
//         `);
//
//             // Display each question
//             questions.forEach((q, index) => {
//                 const questionDiv = getQuestionWhileTaking(q);
//
//                 questionDiv.classList.add("question");
//
//                 document.body.appendChild(questionDiv);
//             });
//
//
//         })
//         .catch(error => {
//             console.error("Fetch failed", error);
//         });
// });



document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const quizId = params.get("id");

    fetch("/quiz", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `id=${encodeURIComponent(quizId)}`
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                console.error("Error loading quiz: " + data.message);
                return;
            }

            const questions = data.questions;
            const isRandom = data.israndom;
            const singlePage = data.singlepage;
            const immediateCorrection = data.immediatecorrection;
            const quizResultId = data.quizresultid;

            if (isRandom) {
                shuffleArray(questions);
            }

            document.body.insertAdjacentHTML("afterbegin", `
                <h2>${data.title}</h2>
                <p>${data.description}</p>
            `);

            if (singlePage) {
                // Render all questions at once
                const questionDivs = []; // store divs here

                // Render all questions at once
                questions.forEach((q) => {
                    const questionDiv = getQuestionWhileTaking(q);
                    questionDiv.classList.add("question");
                    document.body.appendChild(questionDiv);
                    questionDivs.push({ div: questionDiv, question: q });
                });

                const submitBtn = document.createElement("button");
                submitBtn.textContent = "Submit All";
                document.body.appendChild(submitBtn);
                submitBtn.onclick = () => {
                    let totalScore = 0;
                    let maxScore = 0;

                    questionDivs.forEach(({ div, question }) => {
                        const earned = evaluateAnswer(div, question, quizResultId); // same function as in multi-page
                        totalScore += earned;
                        maxScore += question.weight || 1;
                    });

                    // Optional: hide button
                    submitBtn.style.display = "none";

                    // Show result
                    const resultDiv = document.createElement("div");
                    resultDiv.innerHTML = `<h3>Your Score: ${totalScore} / ${maxScore}</h3>`;
                    document.body.appendChild(resultDiv);
                    // Optional: send to backend
                };

            } else {

                 let totalscore = 0;
                 let maxscore = 0;
                // Multi-page mode
                let currentIndex = 0;

                function renderQuestion(index) {
                    document.body.innerHTML = `
                        <h2>${data.title}</h2>
                        <p>${data.description}</p>
                    `;

                    const q = questions[index];
                    const qDiv = getQuestionWhileTaking(q, index);
                    qDiv.classList.add("question");
                    document.body.appendChild(qDiv);

                    const submitBtn = document.createElement("button");
                    submitBtn.textContent = "Submit";

                    submitBtn.onclick = () => {
                        const points = evaluateAnswer(qDiv, q, quizResultId); // you must define this function
                        totalscore += points;
                        maxscore += q.weight;
                        if (immediateCorrection) {
                            submitBtn.style.display = "none"; // hide submit button

                            const feedback = document.createElement("div");
                            feedback.textContent = points > 0 ? "Correct!" : "Incorrect!";
                            document.body.appendChild(feedback);

                            const nextBtn = document.createElement("button");
                            nextBtn.textContent = "Next Question";
                            nextBtn.onclick = () => {
                                qDiv.remove();
                                feedback.remove();
                                nextBtn.remove();
                                nextQuestion();
                            };
                            document.body.appendChild(nextBtn);
                        } else {
                            nextQuestion();
                        }
                    };

                    document.body.appendChild(submitBtn);
                }

                function nextQuestion() {
                    currentIndex++;
                    if (currentIndex < questions.length) {
                        renderQuestion(currentIndex);
                    } else {
                        showFinalSubmit();
                    }
                }

                function showFinalSubmit() {
                    document.body.innerHTML = "<h3>All questions completed!</h3>";
                    const finalBtn = document.createElement("button");
                    finalBtn.textContent = "Submit Quiz";
                    document.body.appendChild(finalBtn);
                    finalBtn.onclick = () => {
                        const resultDiv = document.createElement("div");
                        resultDiv.innerHTML = `<h3>Your Score: ${totalscore} / ${maxscore}</h3>`;
                        document.body.appendChild(resultDiv);
                        finalBtn.remove();
                    };


                    const homeLink = document.createElement("a");
                    homeLink.href = "/home";
                    homeLink.textContent = "Go Home";
                    homeLink.style.display = "block";  // makes it appear on a new line
                    homeLink.style.marginTop = "1em";
                    document.body.appendChild(homeLink);

                }

                renderQuestion(currentIndex);
            }
        })
        .catch(error => {
            console.error("Fetch failed", error);
        });
});

// Fisher-Yates Shuffle
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}

// Placeholder: you must implement this to evaluate answers based on your UI
function evaluateAnswer(div, questionData, quizResultId) {
    // This should check if the selected answer is correct based on `questionData`
    // and return true or false
    return 1; // placeholder
}

