export function getTextAnswerWhileTakingDiv(data) {

    const container = document.createElement('div');
    container.className = 'question-container';

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



export async function evalAnswerTextAnswer(div, questionid, quizresultid, userid) {
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
        userAnswer: userAnswer
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
    if (!evaluationResult || !questionData) return;
    if (!evaluationResult.success || !evaluationResult.userAnswer) return;

    const input = div.querySelector('input[type="text"]');
    if (!input) return;

    input.readOnly = true; // Prevent editing after submission

    const isCorrect = evaluationResult.points > 0;
    input.classList.add(isCorrect ? 'correct-choice' : 'incorrect-choice');
}