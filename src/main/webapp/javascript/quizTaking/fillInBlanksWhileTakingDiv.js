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


export async function evalAnswerFillInBlanks(div, questionid, quizresultid, userid, save = true) {
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
    if (!div || !questionData || !userAnswer || questionData.type !== 'FillInBlanks') {
        console.error("Invalid input to populateFillInBlanksDiv");
        return;
    }

    // Extract list of user-entered strings
    let userTexts = [];

    try {
        if (userAnswer.isString && userAnswer.strAnswer?.list?.length > 0) {
            userTexts = userAnswer.strAnswer.list;
        } else {
            console.warn("FillInTheBlanks userAnswer does not contain a valid string list");
            return;
        }
    } catch (err) {
        console.error("Error extracting fill-in-the-blanks answers from userAnswer", err);
        return;
    }

    // Select inputs by class or data attribute
    const inputs = div.querySelectorAll('input.answer-input');

    if (inputs.length === 0) {
        console.warn("No fill-in-the-blanks inputs found in div");
        return;
    }

    // Populate and disable each input
    inputs.forEach((input, index) => {
        input.value = index < userTexts.length ? userTexts[index] : '';
        input.disabled = true;
    });
}

