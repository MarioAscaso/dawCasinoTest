let currentUser = null;

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Configurar botones
    setupEventListeners();

    // 2. Verificar sesión
    const userStored = localStorage.getItem('user');
    if (!userStored) { window.location.href = 'login.html'; return; }
    
    currentUser = JSON.parse(userStored);
    
    // 3. Sincronizar y Renderizar
    await syncUser();
    renderLobby();
});

function setupEventListeners() {
    const btnSettings = document.getElementById('btn-settings');
    const btnLogout = document.getElementById('btn-logout');
    const cardBlackjack = document.getElementById('card-blackjack');

    if(btnSettings) {
        btnSettings.addEventListener('click', () => {
            window.location.href = 'settings.html';
        });
    }

    if(btnLogout) {
        btnLogout.addEventListener('click', logout);
    }

    if(cardBlackjack) {
        cardBlackjack.addEventListener('click', () => {
            window.location.href = 'blackjack.html';
        });
    }
}

async function syncUser() {
    try {
        const res = await fetch(`${CONFIG.API_URL}/users/${currentUser.id}`);
        if(res.ok) {
            currentUser = await res.json();
            localStorage.setItem('user', JSON.stringify(currentUser));
        }
    } catch(e) {
        console.warn("Error sync:", e);
    }
}

function renderLobby() {
    document.getElementById('username-display').innerText = currentUser.username;
    document.getElementById('balance-display').innerText = currentUser.balance;

    const imgEl = document.getElementById('lobby-avatar');
    const flagEl = document.getElementById('lobby-flag');
    
    imgEl.classList.add('hidden');
    flagEl.classList.add('hidden');

    const isFlag = currentUser.avatarType && currentUser.avatarType.length < 10 && currentUser.avatarType !== "IMAGE";
    
    if (isFlag) {
        flagEl.innerText = currentUser.avatarType;
        flagEl.classList.remove('hidden');
    } else {
        const url = currentUser.avatar || `https://api.dicebear.com/9.x/avataaars/svg?seed=${currentUser.username}`;
        imgEl.src = url;
        imgEl.classList.remove('hidden');
    }
}

function logout() {
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}