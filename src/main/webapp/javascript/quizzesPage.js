import {getQuizDiv} from "./getQuizDiv.js";

document.addEventListener('DOMContentLoaded', function() {
    // Initialize page with URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = parseInt(urlParams.get('page')) || 1;
    const creator = urlParams.get('creator') || '';
    const dateFrom = urlParams.get('dateFrom') || '';
    const dateTo = urlParams.get('dateTo') || '';
    const search = urlParams.get('search') || '';
    const sortBy = urlParams.get('sortby') || '';

    // Set form values from URL
    document.getElementById('creator').value = creator;
    document.getElementById('dateFrom').value = dateFrom;
    document.getElementById('dateTo').value = dateTo;
    document.getElementById('search').value = search;
    document.getElementById('sortby').value = sortBy;

    // Load quizzes with current filters
    loadQuizzes(currentPage, { creator, dateFrom, dateTo, search , sortBy});

    // Form submission handler
    document.getElementById('filterForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const filters = {
            creator: document.getElementById('creator').value,
            dateFrom: document.getElementById('dateFrom').value,
            dateTo: document.getElementById('dateTo').value,
            search: document.getElementById('search').value,
            sortBy: document.getElementById("sortby").value
        };

        // Reset to page 1 when filters change
        updateURL(1, filters);
        loadQuizzes(1, filters);
    });

    // Form reset handler
    document.getElementById('filterForm').addEventListener('reset', function() {
        const emptyFilters = {
            creator: '',
            dateFrom: '',
            dateTo: '',
            search: '',
            sortBy:''
        };
        updateURL(1, emptyFilters);
        loadQuizzes(1, emptyFilters);
    });
});



function updateURL(page, filters) {
    const urlParams = new URLSearchParams();
    if (page > 1) urlParams.set('page', page);
    if (filters.creator) urlParams.set('creator', filters.creator);
    if (filters.dateFrom) urlParams.set('dateFrom', filters.dateFrom);
    if (filters.dateTo) urlParams.set('dateTo', filters.dateTo);
    if (filters.search) urlParams.set('search', filters.search);
    if (filters.sortby) urlParams.set('sortby', filters.sortBy);

    const newUrl = window.location.pathname + '?' + urlParams.toString();
    window.history.pushState({ path: newUrl }, '', newUrl);
}

function loadQuizzes(page, filters) {
    const quizzesContainer = document.getElementById('quizzesContainer');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const noResults = document.getElementById('noResults');

    // Show loading, hide other elements
    loadingSpinner.classList.remove('d-none');
    quizzesContainer.classList.add('d-none');
    noResults.classList.add('d-none');

    // Prepare request data
    const requestData = {
        page: page,
        pageSize: 10,
        creator: filters.creator,
        dateFrom: filters.dateFrom,
        dateTo: filters.dateTo,
        searchQuery: filters.search,
        sortBy: filters.sortBy
    };

    // Fetch data from servlet
    fetch('/quizzes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            return response.json();
        })
        .then(data => {
            //console.log(data.quizzes);
            loadingSpinner.classList.add('d-none');

            if (data.quizzes && data.quizzes.length > 0) {
                renderQuizzes(data.quizzes);
                renderPagination(page, data.totalPages, filters);
                quizzesContainer.classList.remove('d-none');
            } else {
                noResults.classList.remove('d-none');
            }
        })
        .catch(error => {
            console.error('Error fetching quizzes:', error);
            loadingSpinner.classList.add('d-none');
            noResults.textContent = 'Error loading quizzes. Please try again.';
            noResults.classList.remove('d-none');
        });
}

async function renderQuizzes(quizzes) {
    const quizzesList = document.getElementById('quizzesList');
    quizzesList.innerHTML = '';
    for (const quiz of quizzes) {
        //console.log(await getQuizDiv(quiz));
        quizzesList.appendChild( await getQuizDiv(quiz));
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

    if (startPage > 1) {
        const firstLi = document.createElement('li');
        firstLi.className = 'page-item';
        firstLi.innerHTML = `<a class="page-link" href="#" data-page="1">1</a>`;
        pagination.appendChild(firstLi);

        if (startPage > 2) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = `<span class="page-link">...</span>`;
            pagination.appendChild(ellipsisLi);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
        pagination.appendChild(pageLi);
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = `<span class="page-link">...</span>`;
            pagination.appendChild(ellipsisLi);
        }

        const lastLi = document.createElement('li');
        lastLi.className = 'page-item';
        lastLi.innerHTML = `<a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a>`;
        pagination.appendChild(lastLi);
    }

    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="#" aria-label="Next" data-page="${currentPage + 1}">
        <span aria-hidden="true">&raquo;</span>
    </a>`;
    pagination.appendChild(nextLi);

    // Add click event listeners to pagination links
    pagination.querySelectorAll('.page-link').forEach(link => {
        if (link.getAttribute('data-page')) {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));

                updateURL(page, filters);
                loadQuizzes(page, filters);

                // Scroll to top of quizzes
                window.scrollTo({
                    top: document.getElementById('quizzesContainer').offsetTop - 20,
                    behavior: 'smooth'
                });
            });
        }
    });
}