export function getMultiChoiceWhileTakingDiv(data) {
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

    // Checkboxes for choices
    const choiceList = document.createElement('div');
    choiceList.className = 'choice-list';

    data.choices.forEach((choice, index) => {
        const label = document.createElement('label');
        label.className = 'choice-item';

        const input = document.createElement('input');
        input.type = 'checkbox';
        input.name = 'multiChoice';
        input.value = index;

        const span = document.createElement('span');
        span.textContent = choice;

        label.appendChild(input);
        label.appendChild(span);
        choiceList.appendChild(label);
    });

    container.appendChild(choiceList);
    return container;
}


export async function evalAnswerMultiChoice(div, questionid, quizresultid, userid, save = true) {
    const checkedBoxes = div.querySelectorAll(`input[type="checkbox"]:checked`);

    if (!checkedBoxes || checkedBoxes.length === 0) {
        return { success: false, message: "No answers selected" };
    }

    const userAnswer = {
        isString: false,
        choices: Array.from(checkedBoxes).map(box => parseInt(box.value))
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


export function highlightCorrectionMultiChoice(div, evaluationResult, questionData) {
    if (!evaluationResult || !questionData || !evaluationResult.success || !evaluationResult.userAnswer) return;

    const userChoices = evaluationResult.userAnswer.choices;
    const correctChoices = questionData.correctChoices || [];

    // Disable all checkboxes
    div.querySelectorAll(`input[type="checkbox"][name="multiChoice"]`).forEach(input => input.disabled = true);

    // Clear previous highlights
    div.querySelectorAll('.correct-choice, .incorrect-choice').forEach(label => {
        label.classList.remove('correct-choice', 'incorrect-choice');
    });

    // Highlight user's selected checkboxes
    userChoices.forEach(choiceIndex => {
        const input = div.querySelector(`input[type="checkbox"][value="${choiceIndex}"]`);
        if (input) {
            const label = input.closest('label');
            const isCorrect = correctChoices.includes(choiceIndex);
            label.classList.add(isCorrect ? 'correct-choice' : 'incorrect-choice');
        }
    });

    // Highlight correct choices not selected by user
    correctChoices.forEach(correctIndex => {
        if (!userChoices.includes(correctIndex)) {
            const input = div.querySelector(`input[type="checkbox"][value="${correctIndex}"]`);
            if (input) {
                const label = input.closest('label');
                label.classList.add('correct-choice');
            }
        }
    });
}

export function populateMultiChoiceDiv(div, questionData, userAnswer) {
    if (!div || !questionData || !questionData.choices || questionData.choices.length === 0 || !userAnswer) {
        console.error("Invalid input to populateMultiChoiceDiv");
        return;
    }

    let selectedIndices = [];

    try {
        if (userAnswer.isString && userAnswer.strAnswer?.list?.length > 0) {
            // Convert string values to their corresponding indices in choices
            selectedIndices = userAnswer.strAnswer.list.map(val => questionData.choices.indexOf(val)).filter(i => i !== -1);
        } else if (!userAnswer.isString && userAnswer.intAnswer?.list?.length > 0) {
            // Use integer values directly as indices
            selectedIndices = userAnswer.intAnswer.list.map(val => parseInt(val)).filter(i => !isNaN(i));
        }
    } catch (err) {
        console.error("Error extracting choices from userAnswer", err);
        return;
    }

    if (selectedIndices.length === 0) {
        console.warn("No valid choices found in userAnswer");
        return;
    }

    // Find all checkboxes
    const checkboxes = div.querySelectorAll('input[type="checkbox"][name="multiChoice"]');

    if (checkboxes.length === 0) {
        console.warn("No checkbox inputs found in div to populate");
        return;
    }

    checkboxes.forEach((checkbox) => {
        const index = parseInt(checkbox.value);
        //checkbox.disabled = true;

        if (selectedIndices.includes(index)) {
            checkbox.checked = true;
        }
    });
}

