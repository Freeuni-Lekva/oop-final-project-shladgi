export function getFillInChoicesWhileTakingDiv(data) {
    const container = document.createElement('div');
    container.className = 'question-container';
    container.setAttribute('data-question-id', `${data.id}`);
    container.setAttribute('data-question-type', `${data.type}`);

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

    // Create answer fields based on correctAnswers or choices
    const answersContainer = document.createElement('div');
    answersContainer.className = 'answers-container';

    // Determine how many answer fields to create
    const answerCount = data.correctAnswers?.length || data.choices?.length || 1;

    for (let i = 0; i < answerCount; i++) {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${i + 1}:`;
        answerGroup.appendChild(label);

        const select = document.createElement('select');
        select.className = 'answer-select'; // Added this class
        select.style.margin = '0 5px';
        select.dataset.answerIndex = i;
        select.required = true;

        // Add default empty option
        const defaultOption = document.createElement('option');
        defaultOption.value = "";
        defaultOption.textContent = "-- Select --";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        select.appendChild(defaultOption);

        // Add options from choices (use choices[i] if available, otherwise empty)
        if (data.choices && data.choices[i]) {
            data.choices[i].forEach((choice, choiceIndex) => {
                const option = document.createElement('option');
                option.value = choiceIndex;
                option.textContent = choice;
                select.appendChild(option);
            });
        }

        answerGroup.appendChild(select);
        answersContainer.appendChild(answerGroup);
    }

    container.appendChild(answersContainer);
    return container;
}


export async function evalAnswerFillInChoices(div, questionid, quizresultid, userid, save = true) {
    const selects = div.querySelectorAll('select.answer-select');
    const answers = Array.from(selects).map(select =>
        select.value === "" ? null : parseInt(select.value)
    );

    if (answers.some(a => a === null)) {
        return { success: false, message: "Please answer all dropdowns" };
    }

    const userAnswer = {
        isString: false,
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
export function highlightCorrectionFillInChoices(div, evaluationResult, questionData) {
    if (!evaluationResult || !questionData) return;
    if (!evaluationResult.success || !evaluationResult.userAnswer) return;

    const selects = div.querySelectorAll('select.answer-select');
    selects.forEach(select => select.disabled = true);

    const userChoices = evaluationResult.userAnswer.choices;
    const correctChoices = questionData.correctIndexes || [];

    selects.forEach((select, index) => {
        const isCorrect = index < correctChoices.length &&
            index < userChoices.length &&
            userChoices[index] === correctChoices[index];

        select.classList.add(isCorrect ? 'correct-answer' : 'incorrect-answer');

        // Add feedback icon
        const feedbackSpan = document.createElement('span');
        feedbackSpan.className = 'feedback-icon';
        select.parentNode.insertBefore(feedbackSpan, select.nextSibling);

        // Show correct answer if wrong
        if (!isCorrect && index < correctChoices.length) {
            const correctOption = select.querySelector(`option[value="${correctChoices[index]}"]`);
            if (correctOption) {
                const correctMarker = document.createElement('div');
                correctMarker.className = 'correct-answer-marker';
                correctMarker.textContent = `Correct: ${correctOption.textContent}`;
                select.parentNode.insertBefore(correctMarker, select.nextSibling);
            }
        }
    });
}


export function populateFillInChoicesDiv(div, questionData, userAnswer) {
    if (!div || !questionData || !userAnswer || questionData.type !== 'FillChoices') {
        console.error("Invalid input to populateFillInChoicesDiv");
        return;
    }

    // Extract selected choice indices from userAnswer
    let selectedIndices = [];

    try {
        if (!userAnswer.isString && userAnswer.intAnswer?.list?.length > 0) {
            selectedIndices = userAnswer.intAnswer.list.map(val => parseInt(val));
        } else {
            console.warn("FillInChoices userAnswer does not contain a valid int list");
            return;
        }
    } catch (err) {
        console.error("Error extracting selected indices from userAnswer", err);
        return;
    }

    const selects = div.querySelectorAll('select.answer-select');

    if (selects.length === 0) {
        console.warn("No <select> elements found in div to populate");
        return;
    }

    selects.forEach((select, index) => {
        const selectedIndex = selectedIndices[index];

        if (selectedIndex !== undefined && select.options[selectedIndex + 1]) {
            // +1 because first option is the default "-- Select --"
            select.selectedIndex = selectedIndex + 1;
        }

       // select.disabled = true;
    });
}


