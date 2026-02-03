document.addEventListener('DOMContentLoaded', () => {
    // --- LÓGICA DE REGISTRO ---
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        // Event Listeners para la UI de Registro
        const btnRandom = document.getElementById('btn-random');
        const optImage = document.getElementById('opt-image');
        const optFlag = document.getElementById('opt-flag');
        const selectFlag = document.getElementById('reg-flag-select');

        // Botón Random (con stopPropagation para no activar el click del padre)
        if(btnRandom) {
            btnRandom.addEventListener('click', (e) => {
                e.stopPropagation();
                randomizeRegAvatar();
            });
        }

        // Click en la caja de Imagen
        if(optImage) {
            optImage.addEventListener('click', () => selectVisualType('IMAGE'));
        }

        // Click en la caja de Bandera
        if(optFlag) {
            optFlag.addEventListener('click', () => selectVisualType('FLAG'));
        }

        // Cambio en el select de bandera
        if(selectFlag) {
            selectFlag.addEventListener('change', (e) => {
                e.stopPropagation(); // Evitar comportamientos raros
                updateFlagPreview();
            });
            // También evitamos que al hacer click en el select se seleccione la caja visualmente si no queremos
            selectFlag.addEventListener('click', (e) => e.stopPropagation());
        }

        // Submit del formulario
        registerForm.addEventListener('submit', handleRegisterSubmit);
    }

    // --- LÓGICA DE LOGIN ---
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLoginSubmit);
    }
});

// --- HANDLERS (Funciones separadas) ---

async function handleRegisterSubmit(e) {
    e.preventDefault();
    const messageBox = setupMessageBox();

    const type = document.querySelector('input[name="visualType"]:checked').value;
    const avatarUrl = document.getElementById('reg-avatar-preview').src;
    const flagCode = document.getElementById('reg-flag-select').value;

    const finalAvatar = (type === 'IMAGE') ? avatarUrl : null;
    const finalType = (type === 'FLAG') ? flagCode : "IMAGE";

    const data = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        avatar: finalAvatar,
        avatarType: finalType
    };

    try {
        const response = await fetch(`${CONFIG.API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        await handleErrors(response);
        showMessage(messageBox, "¡Cuenta creada! Redirigiendo...", "success");
        setTimeout(() => window.location.href = 'login.html', 2000);

    } catch (error) {
        showMessage(messageBox, error.message, "error");
    }
}

async function handleLoginSubmit(e) {
    e.preventDefault();
    const messageBox = setupMessageBox();

    const data = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    try {
        const response = await fetch(`${CONFIG.API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        await handleErrors(response);
        const user = await response.json();
        
        localStorage.setItem('user', JSON.stringify(user));
        showMessage(messageBox, "¡Bienvenido! Entrando...", "success");
        setTimeout(() => window.location.href = 'index.html', 1500);

    } catch (error) {
        showMessage(messageBox, "Credenciales incorrectas", "error");
    }
}

// --- FUNCIONES VISUALES (Movidas desde el HTML) ---
function randomizeRegAvatar() {
    const seed = Math.random().toString(36).substring(7);
    document.getElementById('reg-avatar-preview').src = `https://api.dicebear.com/9.x/avataaars/svg?seed=${seed}`;
    selectVisualType('IMAGE');
}

function updateFlagPreview() {
    const val = document.getElementById('reg-flag-select').value;
    document.getElementById('reg-flag-preview').innerText = val;
    selectVisualType('FLAG');
}

function selectVisualType(type) {
    if(type === 'IMAGE') document.getElementById('radio-image').checked = true;
    else document.getElementById('radio-flag').checked = true;
}

// --- UTILIDADES UI ---
function setupMessageBox() {
    const box = document.getElementById('message-box');
    box.style.display = 'none';
    box.className = '';
    return box;
}

function showMessage(element, text, type) {
    element.innerText = text;
    element.classList.add(type);
    element.style.display = 'block';
}