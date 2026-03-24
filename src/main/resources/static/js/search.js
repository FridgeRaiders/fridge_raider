// Track selected ingredients, the debounce timer, and keyboard position
const selectedIngredients = [];
let debounceTimer = null;
let activeIndex = -1;

// Filter state
let allRecipes = [];
let filteredRecipes = [];
let filtersOpen = false;

// Pagination
const PAGE_SIZE = 9;
let currentPage = 1;

function toggleFilters() {
    filtersOpen = !filtersOpen;
    document.getElementById('filter-popover').classList.toggle('hidden', !filtersOpen);
}

function getActiveFilters() {
    return {
        budget:   document.getElementById('filter-budget')?.checked ?? false,
        servings: document.getElementById('filter-servings')?.value  ?? '',
        prep:     document.getElementById('filter-prep')?.value      ?? '',
        cook:     document.getElementById('filter-cook')?.value      ?? '',
    };
}

function hasActiveFilters(f) {
    return f.budget || f.servings || f.prep || f.cook;
}

function applyFilters() {
    const f = getActiveFilters();
    updateFilterChips(f);
    updateClearBtn(f);

    filteredRecipes = allRecipes.filter(recipe => {
        if (f.budget && !recipe.isBudget) return false;

        if (f.servings) {
            const min = parseInt(f.servings);
            if (f.servings === '5') {
                if (!recipe.servings || recipe.servings < 5) return false;
            } else {
                if (!recipe.servings || recipe.servings < min || recipe.servings > min + 1) return false;
            }
        }

        if (f.prep && recipe.prepTime && recipe.prepTime > parseInt(f.prep)) return false;
        if (f.cook && recipe.cookTime && recipe.cookTime > parseInt(f.cook)) return false;

        return true;
    });

    currentPage = 1;
    renderPage();
}

function clearFilters() {
    document.getElementById('filter-budget').checked  = false;
    document.getElementById('filter-servings').value  = '';
    document.getElementById('filter-prep').value      = '';
    document.getElementById('filter-cook').value      = '';
    applyFilters();
}

function updateClearBtn(f) {
    const active = hasActiveFilters(f);
    document.getElementById('filter-clear-btn').classList.toggle('hidden', !active);
    document.getElementById('filter-active-dot').classList.toggle('hidden', !active);
}

function updateFilterChips(f) {
    const container = document.getElementById('filter-chips');
    container.innerHTML = '';
    const add = (label, resetFn) => {
        const chip = document.createElement('span');
        chip.className = 'flex items-center gap-1.5 text-xs bg-amber-400/15 text-amber-400 border border-amber-400/30 rounded-full px-2.5 py-0.5';
        chip.innerHTML = `${label} <button onclick="${resetFn}" class="hover:text-white transition-colors cursor-pointer"><i class="fa-solid fa-xmark text-[10px]"></i></button>`;
        container.appendChild(chip);
    };
    if (f.budget)   add('Budget friendly', "removeSingleFilter('budget')");
    if (f.servings) add(`${f.servings === '5' ? '5+' : f.servings + '\u2013' + (parseInt(f.servings) + 1)} servings`, "removeSingleFilter('servings')");
    if (f.prep)     add(`Prep \u2264 ${f.prep} mins`, "removeSingleFilter('prep')");
    if (f.cook)     add(`Cook \u2264 ${f.cook} mins`, "removeSingleFilter('cook')");
}

function removeSingleFilter(key) {
    if (key === 'budget')   document.getElementById('filter-budget').checked = false;
    if (key === 'servings') document.getElementById('filter-servings').value  = '';
    if (key === 'prep')     document.getElementById('filter-prep').value      = '';
    if (key === 'cook')     document.getElementById('filter-cook').value      = '';
    applyFilters();
}


// Grab HTML elements once at the top
const input = document.getElementById('ingredient-input');
const dropdown = document.getElementById('search-dropdown');
const tagsContainer = document.getElementById('selected-tags');


// Listen for typing — debounce so we don't fire a request on every keystroke
input.addEventListener('input', function () {
    const query = input.value.trim();

    // If not signed in, show a message and stop here
    if (!isAuthenticated) {
        showAuthMessage();
        return;
    }

    clearTimeout(debounceTimer);

    // Don't bother querying if the input is too short
    if (query.length < 2) {
        closeDropdown();
        return;
    }

    // Wait 300ms after the user stops typing before fetching
    debounceTimer = setTimeout(function () {
        fetchSuggestions(query);
    }, 300);
});


// Small delay so the click event resolves before the dropdown opens
function showAuthMessage() {
    setTimeout(function () {
        dropdown.innerHTML = '';

        const li = document.createElement('li');
        li.className = 'px-4 py-3 text-sm text-amber-400 flex items-center gap-2';
        li.innerHTML = '<i class="fa-solid fa-lock text-xs"></i> Please <a href="/oauth2/authorization/okta" class="underline hover:text-white transition-colors ml-1">sign in</a> to search ingredients.';

        dropdown.appendChild(li);
        dropdown.classList.remove('hidden');
    }, 10);
}


// Fetch matching ingredients from the Spring Boot endpoint
function fetchSuggestions(query) {
    fetch('/search?query=' + encodeURIComponent(query))
        .then(function (response) {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(function (ingredients) {
            renderDropdown(ingredients);
        })
        .catch(function (error) {
            console.error('Search failed:', error);
            closeDropdown();
        });
}


// Build the dropdown list from the results
function renderDropdown(ingredients) {
    dropdown.innerHTML = '';
    activeIndex = -1;

    if (ingredients.length === 0) {
        closeDropdown();
        return;
    }

    ingredients.forEach(function (ingredient, index) {
        const li = document.createElement('li');
        li.textContent = ingredient.name;
        li.dataset.id = ingredient.id;
        li.dataset.index = index;
        li.className = 'px-4 py-2 text-sm text-white cursor-pointer transition-colors';
        li.setAttribute('role', 'option');
        li.setAttribute('aria-selected', 'false');

        li.addEventListener('click', function () {
            selectIngredient({ id: ingredient.id, name: ingredient.name });
        });

        li.addEventListener('mouseenter', function () {
            const items = dropdown.querySelectorAll('li');
            items.forEach(i => i.classList.remove('bg-amber-400/20', 'text-amber-400'));
            li.classList.add('bg-amber-400/20', 'text-amber-400');
            activeIndex = index;
        });

        li.addEventListener('mouseleave', function () {
            li.classList.remove('bg-amber-400/20', 'text-amber-400');
        });

        dropdown.appendChild(li);
    });

    dropdown.classList.remove('hidden');
}


// Add the ingredient to the selected list if it isn't already there
function selectIngredient(ingredient) {
    const alreadyAdded = selectedIngredients.some(function (i) {
        return i.id === ingredient.id;
    });

    if (!alreadyAdded) {
        selectedIngredients.push(ingredient);
        renderTags();
        fetchRecipes();
    }

    input.value = '';
    closeDropdown();
}


// Rebuild the tag bubbles from the selectedIngredients array
function renderTags() {
    tagsContainer.innerHTML = '';

    selectedIngredients.forEach(function (ingredient) {
        const tag = document.createElement('span');
        tag.className = 'flex items-center gap-2 bg-amber-400 border border-amber-300 text-green-900 text-sm font-medium px-4 py-1.5 rounded-full';

        const label = document.createElement('span');
        label.textContent = ingredient.name;

        const removeBtn = document.createElement('button');
        removeBtn.textContent = '\u00d7';
        removeBtn.className = 'ml-2 text-green-900/60 hover:text-green-900 text-lg font-bold leading-none cursor-pointer transition-colors';

        removeBtn.addEventListener('click', function () {
            removeIngredient(ingredient.id);
        });

        tag.appendChild(label);
        tag.appendChild(removeBtn);
        tagsContainer.appendChild(tag);
    });
}


// Remove an ingredient by id and re-render the tags
function removeIngredient(id) {
    const index = selectedIngredients.findIndex(function (i) { return i.id === id; });
    if (index !== -1) {
        selectedIngredients.splice(index, 1);
    }
    renderTags();
    fetchRecipes();
}


// Add button — only accepts ingredients that exist in the dropdown
function addFromInput() {
    const query = input.value.trim();
    if (query.length === 0) return;

    const items = dropdown.querySelectorAll('li');
    for (let i = 0; i < items.length; i++) {
        if (items[i].textContent.toLowerCase() === query.toLowerCase()) {
            selectIngredient({
                id: parseInt(items[i].dataset.id),
                name: items[i].textContent
            });
            return;
        }
    }
}


// Arrow keys move through the dropdown, Enter selects, Escape closes
input.addEventListener('keydown', function (event) {
    const items = dropdown.querySelectorAll('li');

    if (event.key === 'ArrowDown') {
        event.preventDefault();
        activeIndex = Math.min(activeIndex + 1, items.length - 1);
        highlightItem(items);

    } else if (event.key === 'ArrowUp') {
        event.preventDefault();
        activeIndex = Math.max(activeIndex - 1, 0);
        highlightItem(items);

    } else if (event.key === 'Enter') {
        event.preventDefault();
        if (activeIndex >= 0 && items[activeIndex]) {
            items[activeIndex].click();
        }

    } else if (event.key === 'Escape') {
        closeDropdown();
    }
});


// Highlight the active item and clear the rest
function highlightItem(items) {
    items.forEach(function (item) {
        item.classList.remove('bg-amber-400/20', 'text-amber-400');
        item.setAttribute('aria-selected', 'false');
    });

    if (items[activeIndex]) {
        items[activeIndex].classList.add('bg-amber-400/20', 'text-amber-400');
        items[activeIndex].setAttribute('aria-selected', 'true');
        items[activeIndex].scrollIntoView({ block: 'nearest' });
    }
}


// Hide and empty the ingredient dropdown
function closeDropdown() {
    dropdown.classList.add('hidden');
    dropdown.innerHTML = '';
    activeIndex = -1;
}


// Close the ingredient dropdown when clicking outside the search area
document.addEventListener('click', function (event) {
    const searchArea = document.getElementById('ingredient-search');
    if (!searchArea.contains(event.target)) {
        closeDropdown();
    }
});

// Close the filter popover when clicking outside it
document.addEventListener('click', function (event) {
    if (!filtersOpen) return;
    const popover = document.getElementById('filter-popover');
    const btn = document.getElementById('filter-btn');
    if (!popover.contains(event.target) && !btn.contains(event.target)) {
        filtersOpen = false;
        popover.classList.add('hidden');
    }
});

// Show/hide the placeholder and results section based on selected ingredients
function updateResultsVisibility() {
    const placeholder = document.getElementById('recipe-placeholder');
    const resultsSection = document.getElementById('recipe-results-section');

    if (selectedIngredients.length === 0) {
        placeholder.classList.remove('hidden');
        resultsSection.classList.add('hidden');
    } else {
        placeholder.classList.add('hidden');
        resultsSection.classList.remove('hidden');
    }
}

// Build a comma-separated string of ingredient names and fetch matching recipes
function fetchRecipes() {
    updateResultsVisibility();

    if (selectedIngredients.length === 0) {
        allRecipes = [];
        clearRecipes();
        return;
    }

    const names = selectedIngredients.map(i => i.name).join(',');

    fetch('/recipes/search?ingredients=' + encodeURIComponent(names))
        .then(function (response) {
            if (!response.ok) throw new Error('Recipe search failed');
            return response.json();
        })
        .then(function (recipes) {
            allRecipes = recipes;
            applyFilters();
        })
        .catch(function (error) {
            console.error('Recipe fetch error:', error);
        });
}


// Slice the filtered results for the current page and render cards + pagination
function renderPage() {
    const start = (currentPage - 1) * PAGE_SIZE;
    const end   = start + PAGE_SIZE;
    renderRecipes(filteredRecipes.slice(start, end));
    renderPagination();
}


// Build the pagination controls using the static HTML elements in index.html
function renderPagination() {
    const totalPages = Math.ceil(filteredRecipes.length / PAGE_SIZE);
    const nav        = document.getElementById('recipe-pagination');
    const prevBtn    = document.getElementById('page-prev');
    const nextBtn    = document.getElementById('page-next');
    const numbersEl  = document.getElementById('page-numbers');

    if (totalPages <= 1) {
        nav.classList.add('hidden');
        return;
    }

    nav.classList.remove('hidden');

    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => {
        if (currentPage > 1) {
            currentPage--;
            renderPage();
            document.getElementById('recipe-results-section').scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    };

    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => {
        if (currentPage < totalPages) {
            currentPage++;
            renderPage();
            document.getElementById('recipe-results-section').scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    };

    numbersEl.innerHTML = '';

    const delta = 2;
    const range = [];
    for (let i = Math.max(1, currentPage - delta); i <= Math.min(totalPages, currentPage + delta); i++) {
        range.push(i);
    }

    const addDots = () => {
        const dots = document.createElement('span');
        dots.textContent = '\u2026';
        dots.className = 'px-2 text-amber-400/30 text-sm select-none';
        numbersEl.appendChild(dots);
    };

    const addPageBtn = (p) => {
        const b = document.createElement('button');
        b.textContent = p;
        const isActive = p === currentPage;
        b.className = 'px-3 py-1.5 rounded-md text-sm font-medium transition-all cursor-pointer ' +
            (isActive ? 'bg-amber-400 text-green-900 cursor-default' : 'text-amber-400/70 hover:bg-amber-400/10');
        if (!isActive) {
            b.onclick = () => {
                currentPage = p;
                renderPage();
                document.getElementById('recipe-results-section').scrollIntoView({ behavior: 'smooth', block: 'start' });
            };
        }
        numbersEl.appendChild(b);
    };

    if (range[0] > 1) {
        addPageBtn(1);
        if (range[0] > 2) addDots();
    }

    range.forEach(p => addPageBtn(p));

    if (range[range.length - 1] < totalPages) {
        if (range[range.length - 1] < totalPages - 1) addDots();
        addPageBtn(totalPages);
    }
}


// Render recipe cards into the results section
function renderRecipes(recipes) {
    const container = document.getElementById('recipe-results');
    container.innerHTML = '';

    if (recipes.length === 0) {
        container.innerHTML = '<p class="text-amber-100/50 text-sm col-span-3">No recipes found for those ingredients.</p>';
        return;
    }

    // Fetch all saved IDs in one request, then render cards
    const savedIdsPromise = isAuthenticated
        ? fetch('/saved/ids')
            .then(res => {
                if (!res.ok) return [];
                return res.json();
            })
            .then(data => Array.isArray(data) ? data : [])
            .catch(() => [])
        : Promise.resolve([]);

    savedIdsPromise.then(function (savedIds) {
        const template = document.getElementById('recipe-card-template');

        recipes.forEach(function (recipe) {
            const card = template.content.cloneNode(true);
            const cardEl = card.querySelector('.recipe-card');

            // Wire up save button — stop propagation so it doesn't open the modal
            const saveBtn = cardEl.querySelector('.recipe-save-btn');
            saveBtn.dataset.recipeId = recipe.id;
            saveBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                fetch(`/recipes/${recipe.id}/toggle-save`, { method: 'POST' })
                    .then(res => res.json())
                    .then(data => {
                        setSaveBtnState(saveBtn, data.saved);
                        const modalSaveBtn = document.getElementById('modal-save-btn');
                        if (modalSaveBtn && modalSaveBtn.dataset.recipeId == recipe.id) {
                            setSaveBtnState(modalSaveBtn, data.saved);
                        }
                    })
                    .catch(err => console.error('Save failed:', err));
            });

            // Wire up click to open modal
            cardEl.addEventListener('click', function () {
                openModal(recipe);
            });

            // Name and description
            cardEl.querySelector('.recipe-name').textContent        = recipe.name        ?? '\u2014';
            cardEl.querySelector('.recipe-description').textContent = recipe.description ?? '\u2014';

            // Meta
            cardEl.querySelector('.recipe-prep-text').textContent       = recipe.prepTime  ? recipe.prepTime  + ' mins' : '\u2014';
            cardEl.querySelector('.recipe-cook-text').textContent       = recipe.cookTime  ? recipe.cookTime  + ' mins' : '\u2014';
            cardEl.querySelector('.recipe-servings-text').textContent   = recipe.servings  ? recipe.servings  + ' servings' : '\u2014';
            cardEl.querySelector('.recipe-difficulty-text').textContent = getDifficultyLabel(recipe.difficulty);

            // Budget badge
            if (recipe.isBudget) {
                cardEl.querySelector('.recipe-budget').classList.remove('hidden');
            }

            // Match score badge
            const scoreBadge = cardEl.querySelector('.recipe-match-score');
            scoreBadge.textContent = recipe.matchScore + '% match';
            scoreBadge.classList.add(...getScoreClasses(recipe.matchScore));

            // Image
            const img      = cardEl.querySelector('.recipe-image');
            const fallback = cardEl.querySelector('.recipe-image-fallback');
            if (recipe.image) {
                img.src = recipe.image;
                img.alt = recipe.name ?? '';
                fallback.classList.add('hidden');
            } else {
                img.classList.add('hidden');
            }

            // Set initial saved state from the batch response
            setSaveBtnState(saveBtn, savedIds.includes(recipe.id));

            container.appendChild(card);
        });
    });
}


// Clear the results section and hide pagination
function clearRecipes() {
    document.getElementById('recipe-results').innerHTML = '';
    document.getElementById('recipe-pagination').classList.add('hidden');
}