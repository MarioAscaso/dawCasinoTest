let currentUser = null;

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Verificar sesión
    const userStored = localStorage.getItem('user');
    if (!userStored) { window.location.href = 'login.html'; return; }
    
    currentUser = JSON.parse(userStored);

    // 2. Sincronizar datos frescos (importante por si cambió algo)
    await syncUser();

    // 3. Cargar valores actuales en el formulario
    loadCurrentSettings();
});

async function syncUser() {
    try {
        const res = await fetch(`${CONFIG.API_URL}/users/${currentUser.id}`);
        if(res.ok) {
            currentUser = await res.json();
            localStorage.setItem('user', JSON.stringify(currentUser));
        }
    } catch(e) {}
}

function loadCurrentSettings() {
    // Cargar imagen
    if (currentUser.avatar) {
        document.getElementById('set-avatar-preview').src = currentUser.avatar;
    } else {
        randomizeSetAvatar();
    }
    
    // Detectar si usa bandera o imagen actualmente
    const isFlag = currentUser.avatarType && currentUser.avatarType.length < 10 && currentUser.avatarType !== "IMAGE";
    
    if (isFlag) {
        document.getElementById('set-flag-select').value = currentUser.avatarType;
        updateSetFlag();
        selectSetType('FLAG');
    } else {
        selectSetType('IMAGE');
    }
}

// --- INTERACCIÓN DEL FORMULARIO ---

function randomizeSetAvatar() {
    const seed = Math.random().toString(36).substring(7);
    document.getElementById('set-avatar-preview').src = `https://api.dicebear.com/9.x/avataaars/svg?seed=${seed}`;
    selectSetType('IMAGE');
}

function updateSetFlag() {
    const val = document.getElementById('set-flag-select').value;
    document.getElementById('set-flag-preview').innerText = val;
    selectSetType('FLAG');
}

function selectSetType(type) {
    if(type === 'IMAGE') document.getElementById('set-radio-image').checked = true;
    else document.getElementById('set-radio-flag').checked = true;
}

async function saveSettings() {
    const type = document.querySelector('input[name="setType"]:checked').value;
    const avatarUrl = document.getElementById('set-avatar-preview').src;
    const flagCode = document.getElementById('set-flag-select').value;

    const finalAvatar = (type === 'IMAGE') ? avatarUrl : null;
    const finalType = (type === 'FLAG') ? flagCode : "IMAGE";

    try {
        const response = await fetch(`${CONFIG.API_URL}/users/profile`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: currentUser.id,
                avatar: finalAvatar,
                avatarType: finalType,
                // Mantenemos los límites sin tocar
                dailyLossLimit: currentUser.dailyLossLimit,
                sessionTimeLimit: currentUser.sessionTimeLimit
            })
        });

        if (!response.ok) throw new Error("Error al guardar");

        // Actualizar local y volver
        const updatedUser = await response.json();
        localStorage.setItem('user', JSON.stringify(updatedUser));
        
        alert("✅ Perfil actualizado correctamente");
        window.location.href = 'index.html'; // Volver al lobby automáticamente

    } catch (error) {
        alert("Error: " + error.message);
    }
}