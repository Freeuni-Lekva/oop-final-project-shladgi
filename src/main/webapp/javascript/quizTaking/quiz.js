import { getQuestionWhileTaking } from "./questionWhileTaking.js";
import {loadSessionValue} from "../getSessionInfo.js";
import {evalAnswer} from "./answerSaveWhileTaking.js";
import {highlightQuestionDiv} from "./hihglightQuestion.js";

document.addEventListener("DOMContentLoaded", async () => {
    const startTime = Date.now();
    const params = new URLSearchParams(window.location.search);
    const quizId = params.get("id");
    const practiceModeStr = params.get("practice");
    const practiceMode = practiceModeStr === "true";
    const userid = await loadSessionValue("userid");

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

            let questions = data.questions;
            let responses = [];
            const isRandom = data.israndom;
            const singlePage = data.singlepage;
            const immediateCorrection = data.immediatecorrection;
            const quizResultId = data.quizresultid;
            const practicemode = data.practicemode;
            if (!practicemode) {
                //TODO
            }

            if (isRandom) {
                shuffleArray(questions);
            }

            document.body.insertAdjacentHTML("afterbegin", `
                <h2>${data.title}</h2>
                <p>${data.description}</p>
            `);

            if (practiceMode) {
                questions.forEach(q => q.correctCount = 0);
            }

            if (singlePage) {
                let questionDivs = [];

                const renderSinglePage = () => {
                    document.querySelectorAll('.question').forEach(q => q.remove());
                    questionDivs = [];

                    questions.forEach((q) => {
                        const questionDiv = getQuestionWhileTaking(q);
                        questionDiv.classList.add("question");
                        document.body.appendChild(questionDiv);
                        questionDivs.push({div: questionDiv, question: q});
                    });
                };

                renderSinglePage();

                const submitBtn = document.createElement("button");
                submitBtn.textContent = "Submit All";
                document.body.appendChild(submitBtn);

                submitBtn.onclick = async () => {
                    let totalScore = 0;
                    let maxScore = 0;
                    let qid = 0;
                    for (const {div, question} of questionDivs) {
                        const json = await evaluateAnswer(div, question, quizResultId, userid);
                        if(json.success === true){
                            totalScore += json.points;
                            maxScore += question.weight || 1;
                            responses[qid] = json;
                            if (practiceMode && earned > 0) {
                                question.correctCount++;
                            }

                        }else {
                            responses.length = 0;
                            responses = [];

                            // TODO: washale userResultebi databasedan

                            return;
                        }
                        qid++;
                    }




                    if (practiceMode) {
                        questions = questions.filter(q => q.correctCount < 3);
                        if (questions.length > 0) {
                            renderSinglePage();
                            return;
                        }
                    }

                    // TODO if practice mode da single page gachvenos yvela divze pasuxebi da next try
                    // TODO if singlePage ar aris unda gamoitanos yvela da daaupdatos

                    submitBtn.style.display = "none";
                    const resultDiv = document.createElement("div");
                    resultDiv.innerHTML = `<h3>Your Score: ${totalScore} / ${maxScore}</h3>`;
                    document.body.appendChild(resultDiv);
                    const homeLink = document.createElement("a");
                    homeLink.href = "/home";
                    homeLink.textContent = "Go Home";
                    homeLink.style.display = "block";
                    homeLink.style.marginTop = "1em";
                    const endTime = Date.now(); // current time
                    const timeTakenSeconds = Math.floor((endTime - startTime) / 1000); // assuming you store startTime earlier

                    qid = 0;
                    for (const {div, question} of questionDivs) {
                        highlightQuestionDiv(div, responses[qid], question);
                        qid++;
                    }


                    fetch("/updatequizresult", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: new URLSearchParams({
                            quizresultid: quizResultId,
                            userid: userid,               // pass user id if needed
                            totalScore: totalScore,
                            timeTaken: timeTakenSeconds,
                            practice: practiceMode
                        })
                    })

                    document.body.appendChild(homeLink);



                };

            } else {
                let totalscore = 0;
                let maxscore = 0;
                let currentIndex = 0;

                // Initialize correctCount for each question
                if (practiceMode) {
                    questions.forEach(q => q.correctCount = 0);
                }

                const renderQuestion = () => {

                    if (questions.length === 0 || (!practiceMode && currentIndex >= questions.length)) {
                        showFinalSubmit();
                        return;
                    }

                    // Wrap currentIndex to avoid out-of-bounds
                    currentIndex = currentIndex % questions.length;

                    const q = questions[currentIndex];
                    document.body.innerHTML = `
            <h2>${data.title}</h2>
            <p>${data.description}</p>
        `;

                    const qDiv = getQuestionWhileTaking(q, currentIndex);
                    qDiv.classList.add("question");
                    document.body.appendChild(qDiv);

                    const submitBtn = document.createElement("button");
                    submitBtn.textContent = "Submit";

                    submitBtn.onclick = async () => {
                        const json = await evaluateAnswer(qDiv, q, quizResultId, userid);
                        totalscore += json.points;
                        maxscore += q.weight;

                        if (practiceMode && points === q.weight) {
                            q.correctCount++;
                        }

                        const isCompleted = q.correctCount >= 3;

                        if (isCompleted) {
                            questions.splice(currentIndex, 1);
                            currentIndex = currentIndex % questions.length;
                            // Don't increment currentIndex, stay at the same index
                        } else if (practiceMode) {
                            currentIndex = (currentIndex + 1) % questions.length;
                        } else {
                            currentIndex++;
                        }


                        if (immediateCorrection) {
                            submitBtn.style.display = "none";
                            const feedback = document.createElement("div");

                            feedback.textContent = json.points > 0 ? "Correct!" : "Incorrect!";
                            document.body.appendChild(feedback);

                            const nextBtn = document.createElement("button");
                            nextBtn.textContent = "Next Question";
                            nextBtn.onclick = () => {
                                qDiv.remove();
                                feedback.remove();
                                nextBtn.remove();
                                renderQuestion(); // proceed to next
                            };
                            document.body.appendChild(nextBtn);
                        } else {
                            renderQuestion(); // proceed to next directly
                        }
                    };

                    document.body.appendChild(submitBtn);
                };

                const showFinalSubmit = () => {
                    document.body.innerHTML = "<h3>All questions completed!</h3>";
                    const finalBtn = document.createElement("button");
                    finalBtn.textContent = "Submit Quiz";
                    finalBtn.onclick = () => {
                        const resultDiv = document.createElement("div");
                        resultDiv.innerHTML = `<h3>Your Score: ${totalscore} / ${maxscore}</h3>`;
                        document.body.appendChild(resultDiv);
                        finalBtn.remove();
                    };
                    document.body.appendChild(finalBtn);

                    const homeLink = document.createElement("a");
                    homeLink.href = "/home";
                    homeLink.textContent = "Go Home";
                    homeLink.style.display = "block";
                    homeLink.style.marginTop = "1em";
                    document.body.appendChild(homeLink);
                };

                renderQuestion(); // initial render
            }

        })
        .catch(error => {
            console.error("Fetch failed", error);
        });
});

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}

async function evaluateAnswer(div, questionData, quizResultId, userid) {
    const msg = await evalAnswer(questionData.type, div, questionData.id, quizResultId, userid);
   // console.log(msg);
    return msg;  // Replace with real evaluation
}
