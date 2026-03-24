// uses same modal pattern from feed page

document.addEventListener('DOMContentLoaded', function () {
    const cards = document.querySelectorAll('.saved-recipe-card');

    cards.forEach(function (card) {
        card.addEventListener('click', function () {
            const recipe = {
                id: parseInt(card.dataset.recipeId),
                name: card.dataset.name || '',
                description: card.dataset.description || '',
                image: card.dataset.image || '',
                isBudget: card.dataset.budget === 'true',
                difficulty: card.dataset.difficulty || '',
                prepTime: card.dataset.prepTime ? parseInt(card.dataset.prepTime) : null,
                cookTime: card.dataset.cookTime ? parseInt(card.dataset.cookTime) : null,
                servings: card.dataset.servings ? parseInt(card.dataset.servings) : null,
                nutrients: card.dataset.nutrients || '',
                ingredients: card.dataset.ingredients || '',
                steps: card.dataset.steps || ''
            };

            openModal(recipe);
        });
    });
});