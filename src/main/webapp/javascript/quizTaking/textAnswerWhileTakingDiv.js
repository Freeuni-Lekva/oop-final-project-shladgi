export function getTextAnswerWhileTakingDiv(data) {

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

        // Text input for answer
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Type your answer here...';
    input.style.width = '100%';
    input.style.padding = '8px';
    input.style.margin = '10px 0';
    input.style.boxSizing = 'border-box';


    container.appendChild(input);
    return container;

}




export async function evalAnswerTextAnswer(div, questionid, quizresultid, userid) {
    const input = div.querySelector('input[type="text"]');
    if (!input) {
        console.error("No text input found in the question div.");
        return {success: false, message: "Answer input not found."};
    }

    const userAnswer = input.value.trim();
    const userAnswers = [];
    userAnswers.push(userAnswer);

    // Prepare the JSON object as per your structure
    const dataToSend = {
        userId: userid,
        questionId: questionId,
        resultId: resultId,
        userAnswer: {
            isString: true,       // Since this is a text answer
            choices: userAnswers   // For text input, store the string answer in 'choices'
        }
    };

    try {
        const response = await fetch('/saveAnswer', {  // Change URL to your servlet path
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dataToSend)
        });

        if (!response.ok) {
            const error = await response.json();
            return {success: false, message: error.message || 'Server error'};
        }

        const result = await response.json();
        return result;

    } catch (error) {
        console.error('Error sending answer:', error);
        return {success: false, message: error.message};
    }
}