export function getFillInChoicesWhileTakingDiv(data) {
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

export async function evalAnswerFillInChoices(div, questionid, quizresultid, userid) {
    // Get all select dropdowns - now using the correct class
    const selects = div.querySelectorAll('select.answer-select');
    const answers = [];

    // Collect all selected values (indexes)
    selects.forEach(select => {
        if (select.value === "") {
            answers.push(null); // Unanswered
        } else {
            answers.push(parseInt(select.value)); // Selected index
        }
    });

    // Validate all dropdowns were answered
    if (answers.some(a => a === null)) {
        console.error("Not all dropdowns were answered for question", questionid);
        return {success: false, message: "Please answer all dropdowns"};
    }

    const submissionData = {
        userId: userid,
        questionId: questionid,
        resultId: quizresultid,
        userAnswer: {
            isString: false,  // Storing choice indexes
            choices: answers // Array of selected indexes
        }
    };

    try {
        const response = await fetch('/evalAndSaveUserAnswer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(submissionData)
        });

        if (!response.ok) {
            const error = await response.json();
            return {success: false, message: error.message || "Server error"};
        }

        return await response.json();
    } catch (error) {
        console.error('Error submitting answer:', error);
        return {success: false, message: error.message};
    }
}