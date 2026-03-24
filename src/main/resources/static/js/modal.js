// Shared helpers

function getDifficultyLabel(difficulty) {
    return difficulty ?? '\u2014';
}

function getScoreClasses(score) {
    if (score >= 75) {
        return ['bg-green-400', 'text-white'];
    } else if (score >= 40) {
        return ['bg-amber-400', 'text-green-900'];
    } else {
      return ['alert-red'];
    }
}

// Save / Unsave

function toggleSave(recipeId, btn) {
    if (!isAuthenticated) return;

    fetch(`/recipes/${recipeId}/save`, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            setSaveBtnState(btn, data.saved);
            const modalSaveBtn = document.getElementById('modal-save-btn');
            if (modalSaveBtn && modalSaveBtn.dataset.recipeId == recipeId) {
                setSaveBtnState(modalSaveBtn, data.saved);
            }
        })
        .catch(err => console.error('Save failed:', err));
}

function toggleModalSave() {
    const modalSaveBtn = document.getElementById('modal-save-btn');
    const recipeId = modalSaveBtn?.dataset.recipeId;
    if (!recipeId || !isAuthenticated) return;

    fetch(`/recipes/${recipeId}/toggle-save`, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            setSaveBtnState(modalSaveBtn, data.saved);
            document.querySelectorAll('.recipe-save-btn').forEach(btn => {
                if (btn.dataset.recipeId == recipeId) {
                    setSaveBtnState(btn, data.saved);
                }
            });
        })
        .catch(err => console.error('Save failed:', err));
}

function setSaveBtnState(btn, saved) {
    const icon = btn.querySelector('i');
    if (saved) {
        icon.classList.replace('fa-regular', 'fa-solid');
        btn.classList.add('text-amber-400');
        btn.classList.remove('text-amber-400/40');
    } else {
        icon.classList.replace('fa-solid', 'fa-regular');
        btn.classList.remove('text-amber-400');
        btn.classList.add('text-amber-400/40');
    }
}

// Modal

function openModal(recipe) {
    const modal = document.getElementById('recipe-modal');

    // Image
    const img      = document.getElementById('modal-image');
    const fallback = document.getElementById('modal-image-fallback');
    if (recipe.image) {
        img.src = recipe.image;
        img.alt = recipe.name ?? '';
        img.classList.remove('hidden');
        fallback.classList.add('hidden');
    } else {
        img.classList.add('hidden');
        fallback.classList.remove('hidden');
    }

    // Title, description, difficulty
    document.getElementById('modal-title').textContent       = recipe.name        ?? '\u2014';
    document.getElementById('modal-description').textContent = recipe.description ?? '\u2014';
    document.getElementById('modal-difficulty').textContent  = getDifficultyLabel(recipe.difficulty);

    // Budget badge
    document.getElementById('modal-budget').classList.toggle('hidden', !recipe.isBudget);

    // Match score badge
    const scoreBadge = document.getElementById('modal-match-score');
    scoreBadge.textContent = recipe.matchScore + '% match';
    scoreBadge.className = 'text-xs font-bold px-2 py-0.5 rounded-full ' + getScoreClasses(recipe.matchScore).join(' ');

    // Meta
    document.getElementById('modal-prep').textContent     = recipe.prepTime ? recipe.prepTime + ' mins' : '\u2014';
    document.getElementById('modal-cook').textContent     = recipe.cookTime ? recipe.cookTime + ' mins' : '\u2014';
    document.getElementById('modal-servings').textContent = recipe.servings ? recipe.servings + ' servings' : '\u2014';

    // Nutrients
    const nutrientsGrid    = document.getElementById('modal-nutrients-grid');
    const nutrientsSection = document.getElementById('modal-nutrients-section');
    nutrientsGrid.innerHTML = '';

    try {
        const nutrients = typeof recipe.nutrients === 'string'
            ? Object.fromEntries(
                recipe.nutrients.split(',')
                    .map(s => s.trim())
                    .filter(Boolean)
                    .map(entry => entry.split(':'))
              )
            : recipe.nutrients ?? {};

        const entries = Object.entries(nutrients).filter(([, v]) => v && v !== '0g' && v !== '0');

        if (entries.length > 0) {
            nutrientsSection.classList.remove('hidden');
            entries.forEach(function ([key, value]) {
                const cell = document.createElement('div');
                cell.className = 'flex flex-col items-center bg-green-950 rounded-lg px-2 py-2 border border-amber-400/10';
                cell.innerHTML = `
                    <span class="text-white font-semibold text-sm">${value}</span>
                    <span class="text-amber-100/40 text-xs mt-0.5 capitalize">${key}</span>
                `;
                nutrientsGrid.appendChild(cell);
            });
        } else {
            nutrientsSection.classList.add('hidden');
        }
    } catch (e) {
        nutrientsSection.classList.add('hidden');
    }

    // Ingredients
    const ingredientsList = document.getElementById('modal-ingredients-list');
    ingredientsList.innerHTML = '';

    try {
        const ingredients = typeof recipe.ingredients === 'string'
            ? recipe.ingredients.split('|').map(s => s.trim()).filter(Boolean)
            : recipe.ingredients ?? [];

        ingredients.forEach(function (ingredient) {
            const li = document.createElement('li');
            li.className = 'flex items-start gap-2 text-sm text-amber-100/70';
            li.innerHTML = `<i class="fa-solid fa-circle text-amber-400/40 text-[6px] mt-1.5 shrink-0"></i><span>${ingredient}</span>`;
            ingredientsList.appendChild(li);
        });
    } catch (e) {
        ingredientsList.innerHTML = '<li class="text-amber-100/40 text-sm">Ingredients not available.</li>';
    }

    // Steps
    const stepsList = document.getElementById('modal-steps-list');
    stepsList.innerHTML = '';

    try {
        const steps = typeof recipe.steps === 'string'
            ? recipe.steps.split('.').map(s => s.trim()).filter(Boolean)
            : recipe.steps ?? [];

        steps.forEach(function (step, index) {
            const li = document.createElement('li');
            li.className = 'flex items-start gap-3 text-sm text-amber-100/70';
            li.innerHTML = `
                <span class="shrink-0 w-8 h-8 rounded-full bg-amber-400 text-green-900 text-base font-bold flex items-center justify-center">
                    ${index + 1}
                </span>
                <span class="leading-relaxed">${step}</span>
            `;
            stepsList.appendChild(li);
        });
    } catch (e) {
        stepsList.innerHTML = '<li class="text-amber-100/40 text-sm">Method not available.</li>';
    }

    // Show modal and lock body scroll
    modal.classList.remove('hidden');
    document.body.classList.add('overflow-hidden');

    // Save button
    const modalSaveBtn = document.getElementById('modal-save-btn');
    modalSaveBtn.dataset.recipeId = recipe.id;
    setSaveBtnState(modalSaveBtn, false);

    modalSaveBtn.onclick = toggleModalSave;

    if (isAuthenticated) {
        fetch(`/recipes/${recipe.id}/saved`)
            .then(res => res.json())
            .then(data => setSaveBtnState(modalSaveBtn, data.saved))
            .catch(() => {});
    }
}

function closeModal() {
    document.getElementById('recipe-modal').classList.add('hidden');
    document.body.classList.remove('overflow-hidden');
}

// Close modal on Escape key
document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        closeModal();
    }
});