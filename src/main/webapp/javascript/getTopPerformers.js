/**
 * Reusable function to get top performers for a quiz
 * @param {string|number} quizId - The quiz ID
 * @param {number|null} timeHours - Time filter in hours (null for all time)
 * @param {number} amount - Number of top performers to return
 * @returns {Promise<HTMLElement>} - Returns a div element with the top performers list
 */
// Inject styles only once
if (!document.getElementById("top-performers-style")) {
    const style = document.createElement("style");
    style.id = "top-performers-style";
    style.textContent = `
        .top-performers-container {
            margin-top: 1.5rem;
            background-color: #ffffff;
            border-radius: 8px;
            padding: 1rem 1.5rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }

        .top-performers-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .performer-item {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
            align-items: center;
            padding: 0.6rem 0;
            border-bottom: 1px solid #e0e0e0;
            font-size: 0.95rem;
            color: #333;
        }

        .performer-item:last-child {
            border-bottom: none;
        }

        .performer-item .rank {
            font-weight: bold;
            color: #4257B2;
            margin-right: 1rem;
            min-width: 50px;
        }

        .performer-item .user {
            flex: 1;
            color: #2d3b8b;
        }

        .performer-item .score,
        .performer-item .time,
        .performer-item .date {
            margin-left: 1rem;
            font-size: 0.9rem;
            color: #555;
            white-space: nowrap;
        }

        .top-performers-container .error {
            color: #d9534f;
            font-weight: 500;
            text-align: center;
            padding: 0.5rem;
        }
    `;
    document.head.appendChild(style);
}


export async function getTopPerformers(quizId, timeHours, amount = 5) {
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