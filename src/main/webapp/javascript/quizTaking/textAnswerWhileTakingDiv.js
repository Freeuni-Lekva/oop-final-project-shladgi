export function getTextAnswerWhileTakingDiv(data) {

    const container = document.createElement('div');
    container.className = 'question-container';
    container.setAttribute('data-question-id', `${data.id}`);
    container.setAttribute('data-question-type', `${data.type}`);

    // Question text
    const questionText = document.createElement('p');
    questionText.textContent = data.question;
    questionText.className = 'question-text';
    container.appendChild(questionText);

    // Optional image
    if (data.imageLink) {
        const img = document.createElement('img');
        img.src = data.imageLink;
        img.alt = 'Question image';
        img.className = 'question-image';
        container.appendChild(img);
    }

    // Show weight
    if (data.weight !== undefined) {
        const weightInfo = document.createElement('p');
        weightInfo.textContent = `Weight: ${data.weight}`;
        weightInfo.className = 'question-weight';
        container.appendChild(weightInfo);
    }

        // Text input for answer
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Type your answer here...';
    input.style.width = '100%';
    input.style.padding = '8px';
    input.style.margin = '10px 0';
    input.style.boxSizing = 'border-box';


    container.appendChild(input);
    return container;

}



export async function evalAnswerTextAnswer(div, questionid, quizresultid, userid, save = true) {
    const input = div.querySelector('input[type="text"]');
    if (!input || !input.value.trim()) {
        return { success: false, message: "No answer provided" };
    }

    const userAnswer = {
        isString: true,
        choices: [input.value.trim()]
    };

    const submissionData = {
        userId: userid,
        questionId: questionid,
        resultId: quizresultid,
        userAnswer: userAnswer,
        save: save
    };

    try {
        const response = await fetch('/evalAndSaveUserAnswer', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(submissionData)
        });

        const responseData = await response.json();

        if (!responseData.success) {
            return { success: false, message: responseData.message };
        }

        return {
            success: true,
            userAnswer: userAnswer,
            points: responseData.points,
            message: responseData.message
        };

    } catch (error) {
        return { success: false, message: "Network error" };
    }
}


export function highlightCorrectionTextAnswer(div, evaluationResult, questionData) {
    if (!evaluationResult || !questionData || !evaluationResult.success || !evaluationResult.userAnswer) return;

    const input = div.querySelector('input[type="text"]');
    if (!input) return;

    input.readOnly = true;
    const isCorrect = evaluationResult.points > 0;
    input.classList.add(isCorrect ? 'correct-choice' : 'incorrect-choice');

    // Show all possible correct answers next to input if user's answer was wrong
    if (!isCorrect && Array.isArray(questionData.correctAnswers) && questionData.correctAnswers.length > 0) {
        const correctText = questionData.correctAnswers.join(" OR ");
        const span = document.createElement('span');
        span.className = 'correct-answer-text';
        span.textContent = ` (Correct: ${correctText})`;
        input.parentNode.insertBefore(span, input.nextSibling);
    }
}

export function populateTextAnswerDiv(div, questionData, userAnswer) {
    if (!div || !questionData || !userAnswer || questionData.type !== 'TextAnswer') {
        console.error("Invalid input to populateTextAnswerDiv");
        return;
    }

    // Try to extract the string answer
    let userText = '';

    try {
        if (userAnswer.isString && userAnswer.strAnswer?.list?.length > 0) {
            userText = userAnswer.strAnswer.list[0]; // Get the user's text input
        } else {
            console.warn("TextAnswer userAnswer does not contain a string list");
            return;
        }
    } catch (err) {
        console.error("Error extracting text answer from userAnswer", err);
        return;
    }

    // Get the <input type="text"> inside the div
    const input = div.querySelector('input[type="text"]');

    if (!input) {
        console.warn("Text input not found in div");
        return;
    }

    input.value = userText;
    //input.disabled = true; // Make it read-only for review
}


