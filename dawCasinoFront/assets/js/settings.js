let currentUser = null;

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Listeners
    setupEventListeners();

    // 2. Verificar sesión
    const userStored = localStorage.getItem('user');
    if (!userStored) { window.location.href = 'login.html'; return; }
    currentUser = JSON.parse(userStored);

    // 3. Cargar
    await syncUser();
    loadCurrentSettings();
});

function setupEventListeners() {
    const btnSave = document.getElementById('btn-save');
    const btnBack = document.getElementById('btn-back');
    
    // Opciones visuales
    const optImage = document.getElementById('opt-set-image');
    const optFlag = document.getElementById('opt-set-flag');
    const btnRandom = document.getElementById('btn-set-random');
    const selectFlag = document.getElementById('set-flag-select');

    if(btnSave) btnSave.addEventListener('click', saveSettings);
    
    if(btnBack) btnBack.addEventListener('click', () => {
        window.location.href = 'index.html';
    });

    if(btnRandom) {
        btnRandom.addEventListener('click', (e) => {
            e.stopPropagation();
            randomizeSetAvatar();
        });
    }

    if(optImage) optImage.addEventListener('click', () => selectSetType('IMAGE'));
    if(optFlag) optFlag.addEventListener('click', () => selectSetType('FLAG'));

    if(selectFlag) {
        selectFlag.addEventListener('change', updateSetFlag);
        selectFlag.addEventListener('click', (e) => e.stopPropagation());
    }
}

async function syncUser() {
    try {
        const res = await fetch(`${CONFIG.API_URL}/users/${currentUser.id}`);
        if(res.ok) {
            currentUser = await res.json();
            localStorage.setItem('user', JSON.stringify(currentUser));
        }
    } catch(e) {}
}

// ... (El resto de funciones loadCurrentSettings, randomizeSetAvatar, etc. se mantienen igual, 
// solo asegúrate de que NO se llamen desde el HTML) ...

// ... COPIA AQUÍ las funciones loadCurrentSettings, randomizeSetAvatar, updateSetFlag, selectSetType, saveSettings del anterior settings.js ...
// ... Son idénticas a las que te pasé antes, pero ahora se activan con los listeners de arriba.
// (Te las pongo resumidas para no alargarme, pero usa el código lógico del mensaje anterior)

function loadCurrentSettings() { /* ... */ }
function randomizeSetAvatar() { /* ... */ }
function updateSetFlag() { /* ... */ }
function selectSetType(type) {
    if(type === 'IMAGE') document.getElementById('set-radio-image').checked = true;
    else document.getElementById('set-radio-flag').checked = true;
}
async function saveSettings() { /* ... */ }