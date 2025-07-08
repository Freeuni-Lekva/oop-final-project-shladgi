// Export the function so it can be imported in other files
export async function getTopPerformers(quizId, hours = null, amount = 10) {

    // Create a container div element to hold all the leaderboard content
    const container = document.createElement('div');
    // Add CSS class for styling
    container.className = 'top-performers-container';

    // Create a heading element for the leaderboard
    const header = document.createElement('h3');
    // Set heading text based on whether we're showing all-time or time-limited results
    header.textContent = hours ? `Top Performers (Last ${hours} Hours)` : 'Top Performers (All Time)';
    // Add the heading to the container
    container.appendChild(header);

    // Create an unordered list element to display the performers
    const list = document.createElement('ul');
    // Add CSS class for styling
    list.className = 'top-performers-list';
    // Add the list to the container
    container.appendChild(list);

    // Start try-catch block to handle potential errors
    try {
        // Build the API URL with required parameters
        let url = `/api/topPerformers?quizId=${quizId}&limit=${amount}`;
        // Add hours parameter if specified
        if (hours) url += `&hours=${hours}`;

        // Make the API request
        const response = await fetch(url);
        // Throw error if response is not OK (status code 200-299)
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        // Parse the JSON response
        const performers = await response.json();

        // Handle case when no performers exist
        if (performers.length === 0) {
            const emptyMsg = document.createElement('p');
            emptyMsg.textContent = 'No top performers yet';
            list.appendChild(emptyMsg);
        } else {
            // Loop through each performer and create list items
            performers.forEach(performer => {
                const item = document.createElement('li');
                // Create HTML content for each performer
                item.innerHTML = `
                    <span class="user">User ${performer.userId}</span>
                    <span class="score">Score: ${performer.score.toFixed(1)}</span>
                `;
                // Add the item to the list
                list.appendChild(item);
            });
        }
    } catch (error) {
        // Handle any errors that occur during the process
        console.error('Error loading top performers:', error);
        // Display error message to user
        container.innerHTML = '<p class="error">Failed to load leaderboard</p>';
    }

    // Return the fully constructed container element
    return container;
}