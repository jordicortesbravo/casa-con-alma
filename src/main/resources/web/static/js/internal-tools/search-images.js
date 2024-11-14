document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');
    const keywordsSelect = document.getElementById('keywordsSelect');
    const hasRightsCheck = document.getElementById('hasRightsCheck');
    const imageGallery = document.getElementById('imageGallery');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const relatedImagesModal = new bootstrap.Modal(document.getElementById('relatedImagesModal'));
    const relatedImagesGallery = document.getElementById('relatedImagesGallery');
    const loadMoreRelatedBtn = document.getElementById('loadMoreRelatedBtn');
    let page = 1;
    let relatedPage = 1;
    let currentImageId = null;

    // Perform search
    searchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        page = 1;  // Reset page to 1 on new search
        performSearch();
    });

    loadMoreBtn.addEventListener('click', () => {
        page++;
        performSearch();
    });

    function performSearch() {
        const query = searchInput.value.trim();
        const keywords = Array.from(keywordsSelect.selectedOptions).map(option => option.value).join(',');
        const hasRights = hasRightsCheck.checked;

        fetch(`http://localhost:8083/images/search?query=${query}&keywords=${keywords}&hasRights=${hasRights}&page=${page}`)
            .then(response => response.json())
            .then(data => {
                if (page === 1) {
                    imageGallery.innerHTML = '';  // Clear gallery for new search
                }
                displayImages(data);
            })
            .catch(error => console.error('Error fetching images:', error));
    }

    function displayImages(images) {
        images.forEach(image => {
            const col = document.createElement('div');
            col.classList.add('col-md-3');

            const card = document.createElement('div');
            card.classList.add('card');
            card.setAttribute('title', image.description);

            const img = document.createElement('img');
            img.src = image.thumbnailUrl;
            img.alt = image.description;
            img.classList.add('card-img-top');
            img.dataset.imageId = image.id;

            img.addEventListener('contextmenu', (e) => {
                e.preventDefault();
                showContextMenu(e, image.id);
            });

            card.appendChild(img);
            col.appendChild(card);
            imageGallery.appendChild(col);
        });
    }

    function showContextMenu(event, imageId) {
        const contextMenu = document.createElement('div');
        contextMenu.classList.add('context-menu');

        const copyIdBtn = document.createElement('button');
        copyIdBtn.textContent = 'Copy ID';
        copyIdBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(imageId);
        });

        const viewRelatedBtn = document.createElement('button');
        viewRelatedBtn.textContent = 'View Related';
        viewRelatedBtn.addEventListener('click', () => {
            currentImageId = imageId;
            relatedPage = 1;
            relatedImagesGallery.innerHTML = '';
            loadRelatedImages();
            relatedImagesModal.show();
        });

        contextMenu.appendChild(copyIdBtn);
        contextMenu.appendChild(viewRelatedBtn);
        document.body.appendChild(contextMenu);

        contextMenu.style.top = `${event.clientY}px`;
        contextMenu.style.left = `${event.clientX}px`;
        contextMenu.style.display = 'block';

        document.addEventListener('click', () => {
            contextMenu.remove();
        }, { once: true });
    }

    loadMoreRelatedBtn.addEventListener('click', () => {
        relatedPage++;
        loadRelatedImages();
    });

    function loadRelatedImages() {
        fetch(`http://localhost:8083/crawler/images/related?imageId=${currentImageId}&page=${relatedPage}`)
            .then(response => response.json())
            .then(data => displayRelatedImages(data))
            .catch(error => console.error('Error fetching related images:', error));
    }

    function displayRelatedImages(images) {
        images.forEach(image => {
            const col = document.createElement('div');
            col.classList.add('col-md-3');

            const card = document.createElement('div');
            card.classList.add('card');
            card.setAttribute('title', image.description);

            const img = document.createElement('img');
            img.src = image.thumbnailUrl;
            img.alt = image.description;
            img.classList.add('card-img-top');

            card.appendChild(img);
            col.appendChild(card);
            relatedImagesGallery.appendChild(col);
        });
    }
});
