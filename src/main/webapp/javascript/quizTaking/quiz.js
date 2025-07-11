    import {getQuestionWhileTaking} from "./questionWhileTaking.js";
    import {loadSessionValue} from "../getSessionInfo.js";
    import {evalAnswer} from "./answerSaveWhileTaking.js";
    import {highlightQuestionDiv} from "./hihglightQuestion.js";
    import {loadSavedAnswers, startAutoSave, stopSavingGracefully} from "./quizAutoSave.js";
    import {checkAchievements} from "../achievement.js"

    document.addEventListener("DOMContentLoaded", async () => {
        const startTime = Date.now();
        const params = new URLSearchParams(window.location.search);
        const quizId = params.get("id");
        const practiceModeStr = params.get("practice");
        const practiceMode = practiceModeStr === "true";
        const userid = await loadSessionValue("userid");
        const quizContainer = document.getElementById("quiz-container");
        const bottomContainer = document.getElementById("bottom-container");
        const bigMessageContainer = document.getElementById("big-message-container");
        const smallMessageContainer = document.getElementById("small-message-container")

        const homeLink = document.createElement("a");
        homeLink.href = "/home";
        homeLink.textContent = "Go Home";
        homeLink.style.display = "block";
        homeLink.style.marginTop = "1em";

        console.log(bigMessageContainer);

        const  showSmallMessage = (isOk, message) => {
            smallMessageContainer.classList.remove("error-message", "success-message");
            if(isOk) smallMessageContainer.classList.add("success-message");
            else smallMessageContainer.classList.add("error-message");
            smallMessageContainer.innerText = message;
        };

        document.getElementById("back-to-quiz-btn").href = `/startQuiz?id=${quizId}`;

        const body = new URLSearchParams({
            id: quizId,
            practiceMode : practiceMode,
        });





        fetch("/quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: body.toString()
        })
            .then(response => response.json())
            .then(data => {
                if (!data.success) {
                    bigMessageContainer.innerText = data.message;
                    bigMessageContainer.classList.add("error-message");

                    if (data.showLink === true) {
                        const runningQuizId = data.runningQuizId;
                        const runningQuizButtn = document.createElement("a");
                        runningQuizButtn.textContent = "Go to Running Quiz";
                        runningQuizButtn.href = `startQuiz?id=${runningQuizId}`;  // set default link if none provided
                        runningQuizButtn.classList.add("btn", "btn-primary");  // Bootstrap styling
                        runningQuizButtn.style.marginTop = "1em";

                        bigMessageContainer.appendChild(runningQuizButtn);
                    }


                    return;
                }

                let questions = data.questions;
                let responses = [];
                const isRandom = data.israndom;
                const singlePage = data.singlepage;
                const immediateCorrection = data.immediatecorrection;
                const quizResultId = data.quizresultid;
                const allQuestionsDiv = document.getElementById("all-questions-container");

                const resultLink = document.createElement("a");
                resultLink.href = `/quizResult?id=${quizResultId}`;
                resultLink.textContent = "Detailed Result";
                resultLink.style.display = "block";
                resultLink.style.marginTop = "1em";


                if (isRandom) {
                    shuffleArray(questions);
                }

                quizContainer.insertAdjacentHTML("afterbegin", `
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

                            if(practiceMode) addCorrectCount(q, questionDiv);

                            allQuestionsDiv.appendChild(questionDiv);
                            questionDivs.push({div: questionDiv, question: q});
                        });
                    };

                    renderSinglePage();
                    loadSavedAnswers(quizResultId);
                    startAutoSave(30, quizResultId, userid);


                    const submitBtn = document.createElement("button");
                    submitBtn.textContent = "Submit All";
                    bottomContainer.appendChild(submitBtn);

                    submitBtn.onclick = async () => {
                        let totalScore = 0;
                        let maxScore = 0;
                        let qid = 0;
                        for (const {div, question} of questionDivs) {
                            const json = await evaluateAnswer(div, question, quizResultId, userid);
                            if (json.success === true) {
                                div.classList.remove("invalid-input");
                                totalScore += json.points;
                                maxScore += question.weight || 1;
                                responses[qid] = json;
                                if (practiceMode && json.points === question.weight) {
                                    question.correctCount++;
                                    addCorrectCount(question, div);
                                }
                                smallMessageContainer.innerText = "";

                            } else {
                                responses.length = 0;
                                responses = [];

                                div.classList.add("invalid-input");

                                showSmallMessage(false, json.message);

                                deleteUserAnswersForResult(quizResultId);

                                return;
                            }
                            qid++;
                        }
                        submitBtn.style.display = "none";


                        const resultDiv = getResultDiv(totalScore, maxScore, practiceMode);





                        const endTime = Date.now(); // current time
                        const timeTakenSeconds = Math.floor((endTime - startTime) / 1000); // assuming you store startTime earlier
                        qid = 0;

                        for (const {div, question} of questionDivs) {
                            highlightQuestionDiv(div, responses[qid], question);
                            qid++;
                        }

                        if (practiceMode) {
                            questions = questions.filter(q => q.correctCount < 3);
                            if (questions.length > 0) {
                                const nextTryBtn = document.createElement("button");
                                nextTryBtn.textContent = "Next Try";
                                nextTryBtn.style.marginTop = "1em";
                                nextTryBtn.onclick = () => {
                                    nextTryBtn.remove();
                                    submitBtn.style.display = "block";
                                    renderSinglePage();
                                };
                                bottomContainer.appendChild(nextTryBtn);
                                return;
                            }else{
                                bottomContainer.appendChild(resultDiv);
                            }
                        }else bottomContainer.appendChild(resultDiv);
                        stopSavingGracefully();
                        await saveResult(quizResultId, userid, totalScore, timeTakenSeconds, practiceMode);

                        bottomContainer.appendChild(homeLink);
                        if(!practiceMode)bottomContainer.appendChild(resultLink);

                        if(!practiceMode) await checkAchievements(userid, "take");
                    };

                } else {
                    let totalscore = 0;
                    let maxscore = 0;
                    let currentIndex = 0;
                    let originalQuestions = [...questions];
                    // Initialize correctCount for each question
                    if (practiceMode) {
                        questions.forEach(q => q.correctCount = 0);
                    }
                    for(let i = 0; i < questions.length; i++){
                        questions[i].arrayId = i;
                    }

                    const renderQuestion = () => {
                        console.log("Current id " + currentIndex + "total " + questions.length)
                        if (questions.length === 0 || (!practiceMode && currentIndex >= questions.length)) {
                            showFinalSubmit();
                            return;
                        }

                        // Wrap currentIndex to avoid out-of-bounds
                        currentIndex = currentIndex % questions.length;

                        const q = questions[currentIndex];

                        const qDiv = getQuestionWhileTaking(q, currentIndex);
                        qDiv.classList.add("question");

                        if(practiceMode) addCorrectCount(q, qDiv);

                        allQuestionsDiv.appendChild(qDiv);

                        const submitBtn = document.createElement("button");
                        submitBtn.textContent = "Submit";

                        submitBtn.onclick = async () => {
                            const json = await evaluateAnswer(qDiv, q, quizResultId, userid);
                            if(!json.success){
                                qDiv.classList.add("invalid-input");
                                showSmallMessage(false, json.message);
                                return;
                            }
                            smallMessageContainer.innerText = "";
                            qDiv.classList.remove("invalid-input");
                            responses[q.arrayId] = json;
                            totalscore += json.points;
                            maxscore += q.weight;



                            if (practiceMode && json.points === q.weight) {
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

                            submitBtn.remove();

                            if (immediateCorrection) {
                                highlightQuestionDiv(qDiv, json, q);
                                addCorrectCount(q, qDiv);
                                const nextBtn = document.createElement("button");
                                nextBtn.textContent = "Next Question";
                                nextBtn.onclick = () => {
                                    qDiv.remove();
                                    nextBtn.remove();
                                    renderQuestion(); // proceed to next
                                };
                                bottomContainer.appendChild(nextBtn);
                            } else {
                                qDiv.remove();
                                renderQuestion(); // proceed to next directly
                            }
                        };

                        bottomContainer.appendChild(submitBtn);
                    };

                    const showFinalSubmit = () => {
                        allQuestionsDiv.innerHTML = "<h3>All questions completed!</h3>";
                        const finalBtn = document.createElement("button");
                        finalBtn.textContent = "Submit And View Result";
                        finalBtn.onclick = async () => {
                            const resultDiv = getResultDiv(totalscore, maxscore, practiceMode);
                            bottomContainer.appendChild(resultDiv);
                            finalBtn.remove();

                            const endTime = Date.now(); // current time
                            const timeTakenSeconds = Math.floor((endTime - startTime) / 1000);
                            await saveResult(quizResultId, userid, totalscore, timeTakenSeconds, practiceMode);


                            bottomContainer.appendChild(homeLink);
                            if (!practiceMode) bottomContainer.appendChild(resultLink);
                        };
                        bottomContainer.appendChild(finalBtn);


                    };

                    renderQuestion();
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



    export async function deleteUserAnswersForResult(resultId) {
        try {
            const response = await fetch('/delete-user-answers-for-result', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `resultId=${encodeURIComponent(resultId)}`
            });

            console.log(response);
            const json = await response.json();

            if (json.success) {
                console.log(`Deleted ${json.deleted} user answers.`);
            } else {
                console.error("Failed to delete answers:", json.message);
            }
        } catch (error) {
            console.error("Fetch error:", error);
        }
    }

    function getResultDiv(totalScore, maxScore, isPractice){
        const prevResDiv = document.getElementById("result-div");
        if(prevResDiv != null) prevResDiv.remove();

        const resultDiv = document.createElement("div");
        resultDiv.id = "result-div";
        if(isPractice) resultDiv.innerHTML = `<h3>Practice Finished</h3>`;
        else resultDiv.innerHTML = `<h3>Your Score: ${totalScore} / ${maxScore}</h3>`;
        return resultDiv;
    }

    function addCorrectCount(q, div){
        const prevCountDisplay = div.querySelector(".correct-count-display");
        if(prevCountDisplay !== null) prevCountDisplay.remove();
        const correctCountDisplay = document.createElement("div");
        correctCountDisplay.classList.add("correct-count-display");
        correctCountDisplay.textContent = `Correct : ${q.correctCount || 0} / 3`;
        correctCountDisplay.style.marginBottom = "1em";
        correctCountDisplay.style.fontSize = "0.9em";
        correctCountDisplay.style.color = "#007bff";
        div.prepend(correctCountDisplay);
    }

    function saveResult(quizResultId, userid, totalScore, timeTakenSeconds, practiceMode){
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
        });
    }



    export async function evaluateAnswer(div, questionData, quizResultId, userid) {
        return await evalAnswer(questionData.type, div, questionData.id, quizResultId, userid);
    }
