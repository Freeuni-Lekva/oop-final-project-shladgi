export function getMultiTextAnswerWhileTakingDiv(data) {
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
        input.placeholder = 'Type your answer here...';
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

export async function evalAnswerMultiTextAnswer(div, questionid, quizresultid, userid, save = true) {
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

export function highlightCorrectionMultiTextAnswer(div, evaluationResult, questionData) {
    if (!evaluationResult || !questionData || !evaluationResult.userAnswer) return;

    const inputs = div.querySelectorAll('input[type="text"]');
    const correctAnswers = questionData.correctAnswers || [];
    const exactMatch = !!questionData.exactMatch;
    const ordered = !!questionData.ordered;

    function normalize(str) {
        return str.toLowerCase().replace(/\s+/g, '');
    }

    function matches(possibleAnswer, userInput) {
        return exactMatch
            ? possibleAnswer === userInput
            : normalize(possibleAnswer) === normalize(userInput);
    }

    // Track which correct answers have been matched
    const matchedCorrectIndices = new Set();

    // First pass: mark all correct answers
    inputs.forEach((input, userIndex) => {
        const userInput = input.value.trim();

        if (ordered) {
            // Ordered: only check the corresponding correct answer
            if (userIndex < correctAnswers.length) {
                const correctGroup = correctAnswers[userIndex];
                if (correctGroup.some(correct => matches(correct, userInput))) {
                    matchedCorrectIndices.add(userIndex);
                }
            }
        } else {
            // Unordered: check against all correct answers
            correctAnswers.forEach((group, groupIndex) => {
                if (!matchedCorrectIndices.has(groupIndex) &&
                    group.some(correct => matches(correct, userInput))) {
                    matchedCorrectIndices.add(groupIndex);
                }
            });
        }
    });

    // Second pass: apply styling and show corrections
    inputs.forEach((input, userIndex) => {
        input.readOnly = true;
        const userInput = input.value.trim();
        let isCorrect = false;
        let correctGroupToShow = null;

        if (ordered) {
            // Ordered mode logic (unchanged)
            if (userIndex < correctAnswers.length) {
                const correctGroup = correctAnswers[userIndex];
                isCorrect = correctGroup.some(correct => matches(correct, userInput));
                correctGroupToShow = correctGroup;
            }
        } else {
            // Unordered mode: check if this input matches any correct answer
            correctAnswers.forEach((group, groupIndex) => {
                if (group.some(correct => matches(correct, userInput))) {
                    isCorrect = true;
                }
            });

            // For incorrect answers, show all unmatched correct answers
            if (!isCorrect) {
                const unmatchedCorrects = correctAnswers
                    .filter((_, i) => !matchedCorrectIndices.has(i))
                    .flat();

                if (unmatchedCorrects.length > 0) {
                    correctGroupToShow = unmatchedCorrects;
                }
            }
        }

        // Apply styling
        input.classList.add(isCorrect ? "correct-choice" : "incorrect-choice");

        // Show correction if needed
        if (!isCorrect && correctGroupToShow) {
            const correctText = correctGroupToShow.join(" OR ");
            const span = document.createElement("span");
            span.className = "correct-answer-text";
            span.textContent = ` (Correct: ${correctText})`;

            // Clean up previous correction if exists
            const existingSpan = input.nextElementSibling;
            if (existingSpan?.classList.contains('correct-answer-text')) {
                existingSpan.remove();
            }

            input.parentNode.insertBefore(span, input.nextSibling);
        }
    });
}



export function populateMultiTextAnswerDiv(div, questionData, userAnswer) {
    if (!div || !questionData || !userAnswer || questionData.type !== 'MultiTextAnswer') {
        console.error("Invalid input to populateMultiTextAnswerDiv");
        return;
    }

    // Extract list of user-entered strings
    let userTexts = [];

    try {
        if (userAnswer.isString && userAnswer.strAnswer?.list?.length > 0) {
            userTexts = userAnswer.strAnswer.list; // Array of strings
        } else {
            console.warn("MultiTextAnswer userAnswer does not contain a string list");
            return;
        }
    } catch (err) {
        console.error("Error extracting multi-text answers from userAnswer", err);
        return;
    }

    // Find all inputs in the div that match the structure
    const inputs = div.querySelectorAll('input.multi-text-answer');

    if (inputs.length === 0) {
        console.warn("No multi-text inputs found in div");
        return;
    }

    // Fill inputs with user answers and disable them
    inputs.forEach((input, index) => {
        if (index < userTexts.length) {
            input.value = userTexts[index];
        } else {
            input.value = '';
        }
        // input.disabled = true;
    });
}



