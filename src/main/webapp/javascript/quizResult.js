import {loadSessionValue} from "./getSessionInfo.js";
import {fetchQuizResultData} from "./userQuizResultsDiv.js"
import {getUserQuizResultsDiv} from "./userQuizResultsDiv.js";

// After loading current result
const quizResultId = parseInt(new URLSearchParams(window.location.search).get("id"));

fetchQuizResultData(quizResultId)
    .then(async data => {
        document.getElementById("title").textContent = data.title;
        document.getElementById("totalscore").textContent = data.totalscore;
        document.getElementById("timetaken").textContent = data.timetaken;
        document.getElementById("creationdate").textContent = data.creationdate;

        // Call with current result info
        const userId = await loadSessionValue("userid");
        console.log(userId);
        if(userId === "") {
                const previousResults = document.getElementById("quiz-result-list");
                const newDiv = document.createElement("div");
                const noResult = `<p><strong>No Result! User Not logged in!</strong></p>`
                newDiv.innerHTML = noResult;
                previousResults.replaceWith(newDiv);

        }
        else {
                const quizId = data.quizid;
                const previousResults = document.getElementById("quiz-result-list");
                const newDiv = await getUserQuizResultsDiv(userId, quizId, quizResultId);
                previousResults.replaceWith(newDiv);
        }
    });


