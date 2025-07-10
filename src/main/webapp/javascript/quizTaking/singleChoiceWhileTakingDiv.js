export function getSingleChoiceWhileTakingDiv(data) {
    // Validate required data
    if (!data || !data.question || !data.choices || data.choices.length === 0) {
        console.error("Invalid single choice question data");
        return document.createElement('div'); // Return empty div as fallback
    }

    const container = document.createElement('div');
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

export async function evalAnswerSingleChoice(div, questionid, quizresultid, userid) {
    // Get the selected radio button
    const selectedRadio = div.querySelector(`input[type="radio"][name="singleChoice-${questionid}"]:checked`);

    if (!selectedRadio) {
        console.error("No answer selected for question", questionid);
        return null;
    }

    // Create the UserAnswer JSON structure
    const userAnswer = {
        isString: false,         // Single choice uses integer indexes
        choices: [parseInt(selectedRadio.value)]  // Wrap in array as Answer expects list
    };


    // Prepare data for submission
    const submissionData = {
        userId: userid,
        questionId: questionid,
        resultId: quizresultid,
        userAnswer: userAnswer
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
            return {success : false, message: error.message || "Server error"};
        }

        return await response.json();
    } catch (error) {
        console.error('Error submitting answer:', error);
        return {success : false, message: error.message};
    }
}


