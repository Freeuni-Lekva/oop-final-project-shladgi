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
    if (!evaluationResult || !questionData) return;
    if (!evaluationResult.success || !evaluationResult.userAnswer) return;

    const inputs = div.querySelectorAll('input[type="text"].answer-input');
    inputs.forEach(input => input.readOnly = true);

    // If server provides per-blank correctness
    if (evaluationResult.details?.perBlankCorrectness) {
        inputs.forEach((input, index) => {
            const isCorrect = evaluationResult.details.perBlankCorrectness[index];
            input.classList.add(isCorrect ? 'correct-answer' : 'incorrect-answer');

            const feedbackSpan = document.createElement('span');
            feedbackSpan.className = 'feedback-icon';
            feedbackSpan.textContent = isCorrect ? '✓' : '✗';
            input.parentNode.insertBefore(feedbackSpan, input.nextSibling);
        });
    }
    // Fallback - highlight based on overall correctness
    else {
        const overallClass = evaluationResult.points > 0 ? 'correct-answer' : 'incorrect-answer';
        inputs.forEach(input => input.classList.add(overallClass));
    }

    // Show correct answers if available
    if (questionData.correctAnswers) {
        const correctAnswersDiv = document.createElement('div');
        correctAnswersDiv.className = 'correct-answers-container';

        questionData.correctAnswers.forEach((answers, index) => {
            const answerDiv = document.createElement('div');
            answerDiv.textContent = `Blank ${index + 1}: ${answers.join(' OR ')}`;
            answerDiv.className = 'correct-answer-marker';
            correctAnswersDiv.appendChild(answerDiv);
        });

        div.appendChild(correctAnswersDiv);
    }
}