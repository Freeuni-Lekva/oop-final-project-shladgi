/**
 * Reusable function to get top performers for a quiz
 * @param {string|number} quizId - The quiz ID
 * @param {number|null} timeHours - Time filter in hours (null for all time)
 * @param {number} amount - Number of top performers to return
 * @returns {Promise<HTMLElement>} - Returns a div element with the top performers list
 */
export async function getTopPerformers(quizId, timeHours, amount) {
    try {
        const params = new URLSearchParams({
            quizId: quizId,
            amount: amount
        });

        if (timeHours !== null) {
            params.append('timeHours', timeHours);
        }

        const response = await fetch("getTopPerformers?" + params.toString());
        const data = await response.json();

        const div = document.createElement("div");
        div.className = "top-performers-container";

        if (data.success) {
            if (data.performers.length === 0) {
                div.innerHTML = "<p>No performers found.</p>";
            } else {
                const ul = document.createElement("ul");
                ul.className = "top-performers-list";

                data.performers.forEach((performer, index) => {
                    const li = document.createElement("li");
                    li.className = "performer-item";
                    li.innerHTML = `
                        <span class="rank">#${index + 1}</span>
                        <span class="user">User ${performer.userId}</span>
                        <span class="score">Score: ${performer.score}</span>
                        <span class="time">Time: ${performer.timeTaken}s</span>
                        <span class="date">Date: ${new Date(performer.date).toLocaleDateString()}</span>
                    `;
                    ul.appendChild(li);
                });

                div.appendChild(ul);
            }
        } else {
            div.innerHTML = `<p class="error">❌ ${data.message}</p>`;
        }

        return div;
    } catch (error) {
        const div = document.createElement("div");
        div.className = "top-performers-container";
        div.innerHTML = `<p class="error">❌ Failed to load top performers: ${error.message}</p>`;
        return div;
    }
}