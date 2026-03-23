let currentOffset = 0;
const PAGE_SIZE = 20;

// Fetch a batch of recipes and append cards to the feed
function loadRecipes() {
    return fetch(`/feed/recipes?offset=${currentOffset}&limit=${PAGE_SIZE}`)
        .then(res => res.json())
        .then(recipes => {
            document.getElementById('feed-loading').classList.add('hidden');

            if (recipes.length === 0 && currentOffset === 0) {
                document.getElementById('feed-container').innerHTML =
                    '<p class="text-amber-100/40 text-sm text-center py-10">No recipes available right now.</p>';
                return;
            }

            // Fetch saved IDs once, then render all cards in this batch
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
                recipes.forEach(recipe => appendFeedCard(recipe, savedIds));
                currentOffset += recipes.length;

                const loadMoreBtn = document.getElementById('load-more-btn');
                if (recipes.length === PAGE_SIZE) {
                    loadMoreBtn.classList.remove('hidden');
                } else {
                    loadMoreBtn.classList.add('hidden');
                }
            });
        })
        .catch(err => {
            console.error('Failed to load feed:', err);
            document.getElementById('feed-loading').textContent = 'Failed to load recipes.';
        });
}

// Build and append a single feed card
function appendFeedCard(recipe, savedIds) {
    const container = document.getElementById('feed-container');

    const card = document.createElement('div');
    card.className = 'feed-card group flex flex-col rounded-2xl bg-green-950 border border-amber-400/10 overflow-hidden hover:border-amber-400/40 hover:shadow-lg hover:shadow-black/30 transition-all duration-300 cursor-pointer';

    card.innerHTML = `
        <div class="relative h-56 bg-green-900/60 overflow-hidden">
            ${recipe.image
                ? `<img src="${recipe.image}" alt="${recipe.name ?? ''}" class="w-full h-full object-cover opacity-80 group-hover:opacity-100 group-hover:scale-105 transition-all duration-500"/>`
                : `<div class="absolute inset-0 flex items-center justify-center text-4xl text-amber-400/20"><i class="fa-solid fa-bowl-food"></i></div>`
            }
            ${recipe.isBudget
                ? `<span class="absolute top-3 right-3 bg-amber-400 text-green-900 text-xs font-bold px-2 py-0.5 rounded-full">Budget</span>`
                : ''
            }
            <button class="feed-save-btn absolute top-3 left-3 w-8 h-8 flex items-center justify-center rounded-full bg-black/40 hover:bg-black/60 text-amber-400/60 hover:text-amber-400 transition-colors cursor-pointer" title="Save recipe">
                <i class="fa-regular fa-bookmark text-sm"></i>
            </button>
        </div>
        <div class="flex flex-col gap-3 p-5">
            <h2 class="text-white font-semibold text-base leading-snug">${recipe.name ?? '—'}</h2>
            <p class="text-amber-100/50 text-sm leading-relaxed line-clamp-2">${recipe.description ?? '—'}</p>
            <div class="border-t border-amber-400/10"></div>
            <div class="flex flex-wrap gap-3 text-xs text-amber-400/70">
                <span class="flex items-center gap-1"><i class="fa-solid fa-clock"></i> ${recipe.prepTime ? recipe.prepTime + ' mins prep' : '—'}</span>
                <span class="flex items-center gap-1"><i class="fa-solid fa-fire-burner"></i> ${recipe.cookTime ? recipe.cookTime + ' mins cook' : '—'}</span>
                <span class="flex items-center gap-1"><i class="fa-solid fa-people-group"></i> ${recipe.servings ? recipe.servings + ' servings' : '—'}</span>
                <span class="flex items-center gap-1"><i class="fa-solid fa-gauge"></i> ${recipe.difficulty ?? '—'}</span>
            </div>
        </div>
    `;

    const saveBtn = card.querySelector('.feed-save-btn');

    setSaveBtnState(saveBtn, savedIds.includes(recipe.id));

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

    card.addEventListener('click', function () {
        openModal(recipe);
    });

    container.appendChild(card);
}

// Load more button
document.getElementById('load-more-btn').addEventListener('click', function () {
    this.textContent = 'Loading...';
    this.disabled = true;
    loadRecipes().finally(() => {
        this.textContent = 'Load more recipes';
        this.disabled = false;
    });
});

// Initial load
document.addEventListener('DOMContentLoaded', loadRecipes);