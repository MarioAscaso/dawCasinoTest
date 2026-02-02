let currentUser = null;

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Verificar sesión local
    const userStored = localStorage.getItem('user');
    if (!userStored) { window.location.href = 'login.html'; return; }
    
    currentUser = JSON.parse(userStored);
    
    // 2. Sincronizar datos frescos del servidor
    await syncUser();
    
    // 3. Renderizar Lobby
    renderLobby();
});

async function syncUser() {
    try {
        const res = await fetch(`${CONFIG.API_URL}/users/${currentUser.id}`);
        if(res.ok) {
            currentUser = await res.json();
            localStorage.setItem('user', JSON.stringify(currentUser));
        }
    } catch(e) {
        console.warn("No se pudo sincronizar usuario:", e);
    }
}

function renderLobby() {
    document.getElementById('username-display').innerText = currentUser.username;
    document.getElementById('balance-display').innerText = currentUser.balance;

    const imgEl = document.getElementById('lobby-avatar');
    const flagEl = document.getElementById('lobby-flag');
    
    imgEl.classList.add('hidden');
    flagEl.classList.add('hidden');

    // Lógica para saber si mostrar Bandera o Imagen
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

// 🗑️ ELIMINADAS: openSettings, closeSettings, saveSettings... (Ahora están en settings.js)