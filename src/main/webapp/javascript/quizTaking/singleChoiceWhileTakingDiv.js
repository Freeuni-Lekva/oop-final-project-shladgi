export function getSingleChoiceWhileTakingDiv(data) {
    // Validate required data
    if (!data || !data.question || !data.choices || data.choices.length === 0) {
        console.error("Invalid single choice question data");
        return document.createElement('div'); // Return empty div as fallback
    }

    const container = document.createElement('div');
    container.setAttribute('data-question-id', `${data.id}`);
    container.setAttribute('data-question-type', `${data.type}`);
    container.className = 'question-container single-choice-question';

    // Question text with aria-label
    const questionText = document.createElement('p');
    questionText.textContent = data.question;
    questionText.className = 'question-text';
    questionText.id = 'question-text-' + Math.random().toString(36).substr(2, 9);
    container.appendChild(questionText);

    // Optional image with alt text
    if (data.imageLink) {
        const imgContainer = document.createElement('div');
        imgContainer.className = 'question-image-container';

        const img = document.createElement('img');
        img.src = data.imageLink;
        img.alt = data.imageAltText || 'Question illustration';
        img.className = 'question-image';
        img.setAttribute('aria-describedby', questionText.id);
        imgContainer.appendChild(img);

        container.appendChild(imgContainer);
    }

    // Show weight if available
    if (data.weight !== undefined) {
        const weightInfo = document.createElement('p');
        weightInfo.textContent = `Weight: ${data.weight}`;
        weightInfo.className = 'question-weight';
        container.appendChild(weightInfo);
    }

  
    // Radio buttons for choices with proper accessibility
    const choiceList = document.createElement('div');
    choiceList.className = 'choice-list';
    choiceList.setAttribute('role', `radiogroup`);
    choiceList.setAttribute('aria-labelledby', questionText.id);

    data.choices.forEach((choice, index) => {
        const choiceId = 'choice-' + Math.random().toString(36).substr(2, 9);

        const label = document.createElement('label');
        label.className = 'choice-item';
        label.htmlFor = choiceId;

        const input = document.createElement('input');
        input.type = 'radio';
        input.id = choiceId;
        input.name = `singleChoice-${data.id}`;
        input.value = index;
        input.required = true; // At least one option must be selected
        input.setAttribute('aria-checked', 'false');
        input.setAttribute('role', 'radio');

        const span = document.createElement('span');
        span.textContent = choice;

        // Add event listener to update aria-checked
        input.addEventListener('change', function() {
            document.querySelectorAll(`input[name="${input.name}"]`).forEach(radio => {
                radio.setAttribute('aria-checked', radio.checked ? 'true' : 'false');
            });
        });


        label.appendChild(input);
        label.appendChild(span);
        choiceList.appendChild(label);
    });

    container.appendChild(choiceList);
    return container;
}

export async function evalAnswerSingleChoice(div, questionid, quizresultid, userid, save = true) {
    const selectedRadio = div.querySelector(`input[type="radio"][name="singleChoice-${questionid}"]:checked`);

    if (!selectedRadio) {
        return { success: false, message: "No answer selected" };
    }

    const userAnswer = {
        isString: false,
        choices: [parseInt(selectedRadio.value)]
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

export function highlightCorrectionSingleChoice(div, evaluationResult, questionData) {
    console.log(div, evaluationResult, questionData);
    if (!evaluationResult || !questionData || !evaluationResult.success || !evaluationResult.userAnswer) return;
    console.log("SKIPPPEEEDD");
    const userChoice = evaluationResult.userAnswer.choices[0];
    const correctId = questionData.correctId;

    // Disable all radio inputs
    const allInputs = div.querySelectorAll(`input[name="singleChoice-${questionData.id}"]`);
    allInputs.forEach(input => input.disabled = true);

    // Clear previous highlights
    div.querySelectorAll('.correct-choice, .incorrect-choice').forEach(label => {
        label.classList.remove('correct-choice', 'incorrect-choice');
    });

    // Highlight user's selected choice
    const userInput = div.querySelector(`input[name="singleChoice-${questionData.id}"][value="${userChoice}"]`);
    if (userInput) {
        const label = userInput.closest('label');
        const isCorrect = userChoice === correctId;
        label.classList.add(isCorrect ? 'correct-choice' : 'incorrect-choice');
    }

    // Also highlight correct choice if user's answer was wrong
    if (userChoice !== correctId && correctId !== undefined) {
        const correctInput = div.querySelector(`input[name="singleChoice-${questionData.id}"][value="${correctId}"]`);
        if (correctInput) {
            const label = correctInput.closest('label');
            label.classList.add('correct-choice');
        }
    }
}

export function populateSingleChoiceDiv(div, questionData, userAnswer) {
    if (!div || !questionData || !questionData.choices || questionData.choices.length === 0 || !userAnswer) {
        console.error("Invalid input to populateSingleChoiceDiv");
        return;
    }

    // Try to extract selected index from jsondata (fallback-safe)
    let selectedIndex = -1;

    try {
        let choices;
        if(userAnswer.isString){
            choices = userAnswer.strAnswer.list;
        }else{
            choices = userAnswer.intAnswer.list;
        }

        if (Array.isArray(choices) && choices.length > 0) {
            if (userAnswer.isString) {
                const selectedValue = choices[0];
                selectedIndex = questionData.choices.indexOf(selectedValue);
            } else {
                selectedIndex = parseInt(choices[0]);
            }
        }
    } catch (err) {
        console.error("Failed to extract user answer index from userAnswer object", err);
        return;
    }
    console.log(selectedIndex)
    if (selectedIndex < 0 || selectedIndex >= questionData.choices.length) {
        console.warn("Selected index out of range or not found");
        return;
    }

    // Set the correct radio input as checked
    const inputSelector = `input[type="radio"][name="singleChoice-${questionData.id}"]`;
    const inputs = div.querySelectorAll(inputSelector);

    if (inputs.length === 0) {
        console.warn("No radio inputs found in div to populate");
    }

    inputs.forEach((input, index) => {
        //input.disabled = true;
        if (parseInt(input.value) === selectedIndex) {
            input.checked = true;
            input.setAttribute("aria-checked", "true");
        } else {
            input.setAttribute("aria-checked", "false");
        }
    });
}


