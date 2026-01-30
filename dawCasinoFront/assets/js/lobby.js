document.addEventListener('DOMContentLoaded', () => {
    checkSession();
});

function checkSession() {
    const userStored = localStorage.getItem('user');

    if (!userStored) {
        // Si no hay usuario, patada al login
        window.location.href = 'login.html';
        return;
    }

    // Si hay usuario, pintamos la info
    const user = JSON.parse(userStored);
    
    // Buscamos los elementos en el HTML (con validación por si acaso)
    const userDisplay = document.getElementById('username-display');
    const balanceDisplay = document.getElementById('balance-display');

    if (userDisplay) userDisplay.innerText = user.username;
    if (balanceDisplay) balanceDisplay.innerText = user.balance;
}

// Función global de Logout (para el botón)
function logout() {
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}