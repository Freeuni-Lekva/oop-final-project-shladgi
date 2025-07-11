export function getFillInBlanksWhileTakingDiv(data) {
    const container = document.createElement('div');
    container.className = 'question-container';

    // Display the question text
    const questionText = document.createElement('div');
    questionText.className = 'question-text';
    questionText.textContent = data.question;
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

    // Create answer fields
    const answersContainer = document.createElement('div');
    answersContainer.className = 'answers-container';

    const answerCount = data.correctAnswers?.length || data.choices?.length || 1;

    for (let i = 0; i < answerCount; i++) {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${i + 1}:`;
        answerGroup.appendChild(label);

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'answer-input';
        input.style.width = '100px';
        input.style.margin = '0 5px';
        input.dataset.answerIndex = i;
        input.required = true;
        answerGroup.appendChild(input);

        answersContainer.appendChild(answerGroup);
    }

    container.appendChild(answersContainer);
    return container;
}


export async function evalAnswerFillInBlanks(div, questionid, quizresultid, userid) {
    const inputs = div.querySelectorAll('input[type="text"].answer-input');
    const answers = Array.from(inputs).map(input => input.value.trim());

    if (answers.length === 0 || answers.every(a => !a)) {
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

export function highlightCorrectionFillInBlanks(div, evaluationResult, questionData) {
    if (!evaluationResult || !questionData || !evaluationResult.success || !evaluationResult.userAnswer) return;

    const inputs = div.querySelectorAll('input[type="text"].answer-input');
    const correctAnswersList = questionData.correctAnswers || [];

    inputs.forEach((input, index) => {
        input.readOnly = true; // Make input read-only after submission

        const userAnswer = input.value.trim();
        const correctAnswers = correctAnswersList[index] || [];

        let isCorrect = false;
        if (questionData.exactMatch) {
            isCorrect = correctAnswers.includes(userAnswer);
        } else {
            const normalizedUser = userAnswer.toLowerCase().replace(/\s+/g, '');
            isCorrect = correctAnswers.some(
                correct => normalizedUser === correct.toLowerCase().replace(/\s+/g, '')
            );
        }

        // Remove prior highlights if any
        input.classList.remove("correct-answer", "incorrect-answer");

        // Apply styling
        input.classList.add(isCorrect ? "correct-answer" : "incorrect-answer");

        // Add correct answer info only if wrong
        if (!isCorrect && correctAnswers.length > 0) {
            const correctAnswerText = document.createElement("span");
            correctAnswerText.className = "correct-answer-text";
            correctAnswerText.textContent = ` (Correct: ${correctAnswers.join(' OR ')})`;
            input.parentNode.appendChild(correctAnswerText);
        }
    });
}


export function populateFillInBlanksDiv(div, questionData, userAnswer) {
    // Validate required data
    if (!div || !questionData || !questionData.question || !userAnswer ||
        !userAnswer.choices) {
        console.error("Invalid data for populating fill-in-blanks question");
        return;
    }

    // Clear the div if it has any content
    div.innerHTML = '';
    div.className = 'question-container';

    // Display the question text
    const questionText = document.createElement('div');
    questionText.className = 'question-text';
    questionText.textContent = questionData.question;
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
    const answersContainer = document.createElement('div');
    answersContainer.className = 'answers-container';

    userAnswer.choices.forEach((answer, index) => {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${index + 1}:`;
        answerGroup.appendChild(label);

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'answer-input';
        input.value = answer;
        input.readOnly = true;
        input.style.width = '100px';
        input.style.margin = '0 5px';
        input.dataset.answerIndex = index;
        answerGroup.appendChild(input);

        answersContainer.appendChild(answerGroup);
    });

    div.appendChild(answersContainer);

    // If we have evaluation data, highlight the correction
    if (userAnswer.points !== undefined) {
        highlightCorrectionFillInBlanks(div, {
            success: true,
            userAnswer: userAnswer,
            points: userAnswer.points
        }, questionData);
    }

    return div;
}
