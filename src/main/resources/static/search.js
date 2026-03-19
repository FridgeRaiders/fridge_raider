// Track selected ingredients, the debounce timer, and keyboard position
const selectedIngredients = [];
let debounceTimer = null;
let activeIndex = -1;

// Grab my HTML elements once at the top
const input = document.getElementById('ingredient-input');
const dropdown = document.getElementById('search-dropdown');
const tagsContainer = document.getElementById('selected-tags');


// Listen for typing — debounce so I don't fire a request on every keystroke
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


// Fetch matching ingredients from my Spring Boot endpoint
function fetchSuggestions(query) {
    fetch('/search?query=' + encodeURIComponent(query))
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
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
        li.className = 'px-4 py-2 text-sm text-white cursor-pointer hover:bg-amber-400/20 transition-colors';

        li.addEventListener('click', function () {
            selectIngredient({ id: ingredient.id, name: ingredient.name });
        });

        dropdown.appendChild(li);
    });

    dropdown.classList.remove('hidden');
}


// Add the ingredient to my selected list if it isn't already there
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


// Rebuild the tag bubbles from my selectedIngredients array
function renderTags() {
    tagsContainer.innerHTML = '';

    selectedIngredients.forEach(function (ingredient) {
        const tag = document.createElement('span');
        tag.className = 'flex items-center gap-2 bg-amber-400 border border-amber-300 text-green-900 text-sm font-medium px-4 py-1.5 rounded-full';

        const label = document.createElement('span');
        label.textContent = ingredient.name;

        const removeBtn = document.createElement('button');
        removeBtn.textContent = '×';
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
        item.classList.remove('bg-amber-400/20');
    });
    if (items[activeIndex]) {
        items[activeIndex].classList.add('bg-amber-400/20');
    }
}


// Hide and empty the dropdown
function closeDropdown() {
    dropdown.classList.add('hidden');
    dropdown.innerHTML = '';
    activeIndex = -1;
}


// Close the dropdown when clicking outside the search area
document.addEventListener('click', function (event) {
    const searchArea = document.getElementById('ingredient-search');
    if (!searchArea.contains(event.target)) {
        closeDropdown();
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
        clearRecipes();
        return;
    }

    const names = selectedIngredients.map(function (i) {
        return encodeURIComponent(i.name);
    }).join(',');

    fetch('/recipes/search?ingredients=' + names)
        .then(function (response) {
            if (!response.ok) throw new Error('Recipe search failed');
            return response.json();
        })
        .then(function (recipes) {
            renderRecipes(recipes);
        })
        .catch(function (error) {
            console.error('Recipe fetch error:', error);
        });
}


// Render recipe cards into the results section
function renderRecipes(recipes) {
    const container = document.getElementById('recipe-results');
    container.innerHTML = '';

    if (recipes.length === 0) {
        container.innerHTML = '<p class="text-amber-100/50 text-sm col-span-3">No recipes found for those ingredients.</p>';
        return;
    }

    const template = document.getElementById('recipe-card-template');

    recipes.forEach(function (recipe) {
        // Clone the template — true means clone all child elements too
        const card = template.content.cloneNode(true);

        // Fill in each named element
        card.querySelector('.recipe-description').textContent = recipe.description;
        card.querySelector('.recipe-ingredients').textContent = recipe.ingredients;
        card.querySelector('.recipe-prep').textContent = (recipe.prepTime ? recipe.prepTime + ' mins prep' : '—');
        card.querySelector('.recipe-cook').textContent = (recipe.cookTime ? recipe.cookTime + ' mins cook' : '—');
        card.querySelector('.recipe-servings').textContent = (recipe.servings ? recipe.servings + ' servings' : '—');

        // Only show the budget badge if isBudget is true
        if (recipe.isBudget) {
            card.querySelector('.recipe-budget').classList.remove('hidden');
        }

        container.appendChild(card);
    });
}

// Clear the results section
function clearRecipes() {
    const container = document.getElementById('recipe-results');
    container.innerHTML = '';
}