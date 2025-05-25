let quantityChart;

async function fetchProducts() {
    const response = await fetch('/api/inventory/products');
    const products = await response.json();
    renderProducts(products);
    renderQuantityChart(products);
}

function renderProducts(products) {
    const tableBody = document.getElementById('productTableBody');
    tableBody.innerHTML = '';
    products.forEach(product => {
        tableBody.innerHTML += `
            <tr>
                <td><img src="${product.imageUrl}" alt="${product.name}"></td>
                <td>${product.name}</td>
                <td>${product.sku}</td>
                <td>${product.quantity}</td>
                <td>${product.quantity > 0 ? 'In Stock' : 'Out of Stock'}</td>
                <td>
                    <button class="btn" onclick="editProduct(${product.id})">Edit</button>
                    <button class="btn" onclick="deleteProduct(${product.id})">Delete</button>
                </td>
            </tr>
        `;
    });
}

function renderQuantityChart(products) {
    const labels = products.map(p => p.name);
    const data = products.map(p => p.quantity);

    if (quantityChart) quantityChart.destroy();

    const ctx = document.getElementById('quantityChart').getContext('2d');
    quantityChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: 'Product Quantities',
                data,
                backgroundColor: 'rgba(37, 99, 235, 0.5)'
            }]
        },
        options: { responsive: true }
    });
}

async function searchProducts() {
    const keyword = document.getElementById('searchInput').value;
    const response = await fetch(`/api/inventory/products/search?keyword=${keyword}`);
    const products = await response.json();
    renderProducts(products);
}

async function deleteProduct(id) {
    if (confirm('Are you sure you want to delete this product?')) {
        await fetch(`/api/inventory/products/${id}`, { method: 'DELETE' });
        fetchProducts();
    }
}

function openAddProductModal() {
    document.getElementById('productModal').style.display = 'flex';
    document.getElementById('productForm').reset();
    document.getElementById('modalTitle').textContent = 'Add Product';
}

function closeProductModal() {
    document.getElementById('productModal').style.display = 'none';
}

document.getElementById('productForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const productId = document.getElementById('productId').value;
    const formData = new FormData(document.getElementById('productForm'));
    const url = productId ? `/api/inventory/products/${productId}` : '/api/inventory/products';
    const method = productId ? 'PUT' : 'POST';

    await fetch(url, { method, body: formData });
    closeProductModal();
    fetchProducts();
});

fetchProducts();
