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

        const input = document.createElement('input');
        input.type = 'text';
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
    // Get all blank inputs
    const inputs = div.querySelectorAll('input[type="text"].answer-input');
    const answers = [];

    // Collect all answers (text values)
    inputs.forEach(input => {
        answers.push(input.value.trim());
    });

    // Validate at least one answer exists
    if (answers.length === 0 || answers.every(a => a === "")) {
        console.error("No answers provided for fill-in-blanks question", questionid);
        return null;
    }

    const submissionData = {
        userId: userid,
        questionId: questionid,
        resultId: quizresultid,
        userAnswer: {
            isString: true,  // Storing text answers
            choices: answers // Array of text responses
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
            return {success : false, message: error.message || "Server error"};
        }

        return await response.json();
    } catch (error) {
        console.error('Error submitting answer:', error);
        return {success : false, message: error.message};
    }


}