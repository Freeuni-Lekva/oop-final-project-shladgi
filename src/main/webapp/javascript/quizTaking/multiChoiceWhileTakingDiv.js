export function getMultiChoiceWhileTakingDiv(data) {
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


export async function evalAnswerMultiChoice(div, questionid, quizresultid, userid) {
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

