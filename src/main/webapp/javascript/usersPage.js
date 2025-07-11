import { getUserDiv } from './userDivSmallJS.js';

document.addEventListener('DOMContentLoaded', function() {
    // Initialize page with URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = parseInt(urlParams.get('page')) || 1;
    const search = urlParams.get('search') || '';
    const friendStatus = urlParams.get('friendStatus') || 'all';

    // Set form values from URL
    document.getElementById('search').value = search;
    document.getElementById('friendStatus').value = friendStatus;

    // Load users with current filters
    loadUsers(currentPage, { search, friendStatus });

    // Form submission handler
    document.getElementById('filterForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const filters = {
            search: document.getElementById('search').value,
            friendStatus: document.getElementById('friendStatus').value
        };

        // Reset to page 1 when filters change
        updateURL(1, filters);
        loadUsers(1, filters);
    });

    // Form reset handler
    document.getElementById('filterForm').addEventListener('reset', function() {
        const emptyFilters = {
            search: '',
            friendStatus: 'all'
        };
        updateURL(1, emptyFilters);
        loadUsers(1, emptyFilters);
    });
});

function updateURL(page, filters) {
    const urlParams = new URLSearchParams();
    if (page > 1) urlParams.set('page', page);
    if (filters.search) urlParams.set('search', filters.search);
    if (filters.friendStatus !== 'all') urlParams.set('friendStatus', filters.friendStatus);

    const newUrl = window.location.pathname + '?' + urlParams.toString();
    window.history.pushState({ path: newUrl }, '', newUrl);
}





async function loadUsers(page, filters) {
    const usersContainer = document.getElementById('usersContainer');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const noResults = document.getElementById('noResults');

    // Show loading, hide other elements
    loadingSpinner.classList.remove('d-none');
    usersContainer.classList.add('d-none');
    noResults.classList.add('d-none');

    try {
        const response = await fetch('/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                page: page,
                pageSize: 10,
                searchQuery: filters.search,
                friendStatus: filters.friendStatus
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log(data);

        loadingSpinner.classList.add('d-none');

        if (data.users && data.users.length > 0) {
            renderUsers(data.users);
            renderPagination(page, data.totalPages, filters);
            usersContainer.classList.remove('d-none');
        } else {
            noResults.classList.remove('d-none');
        }
    } catch (error) {
        console.error('Error fetching users:', error);
        loadingSpinner.classList.add('d-none');
        noResults.textContent = 'Error loading users. Please try again.';
        noResults.classList.remove('d-none');
    }
}

async function renderUsers(users) {
    const usersList = document.getElementById('usersList');
    usersList.innerHTML = '';

    for (const user of users) {

        const userCard = await getUserDiv(user.name);
        usersList.appendChild(userCard);
    }


}

function renderPagination(currentPage, totalPages, filters) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    if (totalPages <= 1) return;

    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link" href="#" aria-label="Previous" data-page="${currentPage - 1}">
        <span aria-hidden="true">&laquo;</span>
    </a>`;
    pagination.appendChild(prevLi);

    // Page numbers
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    // Page number buttons
    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
        pagination.appendChild(pageLi);
    }

    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="#" aria-label="Next" data-page="${currentPage + 1}">
        <span aria-hidden="true">&raquo;</span>
    </a>`;
    pagination.appendChild(nextLi);

    // Add click event listeners
    pagination.querySelectorAll('.page-link').forEach(link => {
        if (link.getAttribute('data-page')) {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));
                updateURL(page, filters);
                loadUsers(page, filters);
            });
        }
    });
}
