export function getMultiTextAnswerWhileTakingDiv(data) {
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

    // Determine number of inputs needed
    const answerCount = data.correctAnswers?.length || data.expectedAnswers || 2; // Default to 2 if not specified

    // Create exactly the needed number of text inputs
    for (let i = 0; i < answerCount; i++) {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${i + 1}:`;
        answerGroup.appendChild(label);

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'multi-text-answer';
        input.dataset.answerIndex = i;
        input.required = true;
        input.style.width = '100%';
        input.style.padding = '8px';
        input.style.margin = '5px 0';
        input.style.boxSizing = 'border-box';
        answerGroup.appendChild(input);

        container.appendChild(answerGroup);
    }

    return container;
}

export async function evalAnswerMultiTextAnswer(div, questionid, quizresultid, userid) {
    const inputs = div.querySelectorAll('input[type="text"]');
    const answers = Array.from(inputs).map(input => input.value.trim()).filter(a => a);

    if (answers.length === 0) {
        return { success: false, message: "No answers provided" };
    }

    const userAnswer = {
        isString: true,
        choices: answers
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

export function highlightCorrectionMultiTextAnswer(div, evaluationResult, questionData) {
    if (!evaluationResult || !questionData || !evaluationResult.success || !evaluationResult.userAnswer) return;

    const inputs = div.querySelectorAll('input[type="text"]');
    const correctAnswers = questionData.correctAnswers || [];

    inputs.forEach((input, index) => {
        input.readOnly = true;

        let isCorrect = false;

        // Debug log

        // First: use server-provided correctness if available
        if (Array.isArray(evaluationResult.details?.perAnswerCorrectness)) {
            isCorrect = evaluationResult.details.perAnswerCorrectness[index] === true;
        }
        // Otherwise: fallback to comparing user input against correct options
        else if (Array.isArray(correctAnswers[index])) {
            const userInput = input.value.trim().toLowerCase();
            const normalizedCorrects = correctAnswers[index].map(ans => ans.trim().toLowerCase());
            isCorrect = normalizedCorrects.includes(userInput);
        }

        input.classList.add(isCorrect ? "correct-choice" : "incorrect-choice");

        // Show correct answers only if incorrect
        if (!isCorrect && Array.isArray(correctAnswers[index]) && correctAnswers[index].length > 0) {
            const correctText = correctAnswers[index].join(" OR ");
            const span = document.createElement("span");
            span.className = "correct-answer-text";
            span.textContent = ` (Correct: ${correctText})`;
            input.parentNode.insertBefore(span, input.nextSibling);
        }
    });
}


export function populateMultiTextAnswerDiv(div, questionData, userAnswer) {
    // Validate required data
    if (!div || !questionData || !questionData.question || !userAnswer ||
        !userAnswer.choices) {
        console.error("Invalid data for populating multi-text answer question");
        return;
    }

    // Clear the div if it has any content
    div.innerHTML = '';
    div.className = 'question-container';

    // Question text
    const questionText = document.createElement('p');
    questionText.textContent = questionData.question;
    questionText.className = 'question-text';
    div.appendChild(questionText);

    // Optional image
    if (questionData.imageLink) {
        const img = document.createElement('img');
        img.src = questionData.imageLink;
        img.alt = 'Question image';
        img.className = 'question-image';
        div.appendChild(img);
    }

    // Show weight
    if (questionData.weight !== undefined) {
        const weightInfo = document.createElement('p');
        weightInfo.textContent = `Weight: ${questionData.weight}`;
        weightInfo.className = 'question-weight';
        div.appendChild(weightInfo);
    }

    // Create answer fields based on user's answers
    userAnswer.choices.forEach((answer, index) => {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${index + 1}:`;
        answerGroup.appendChild(label);

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'multi-text-answer';
        input.value = answer;
        input.readOnly = true;
        input.dataset.answerIndex = index;
        input.style.width = '100%';
        input.style.padding = '8px';
        input.style.margin = '5px 0';
        input.style.boxSizing = 'border-box';
        answerGroup.appendChild(input);

        div.appendChild(answerGroup);
    });

    // If we have evaluation data, highlight the correction
    if (userAnswer.points !== undefined) {
        highlightCorrectionMultiTextAnswer(div, {
            success: true,
            userAnswer: userAnswer,
            points: userAnswer.points
        }, questionData);
    }

    return div;
}


